package com.redpanda.concepto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.infrastructure.local.AppDbContext
import com.redpanda.concepto.infrastructure.local.repository.HotPointLogRepository
import com.redpanda.concepto.infrastructure.local.repository.HotPointRepository
import com.redpanda.concepto.presentation.ui.theme.ConceptoTheme
import com.redpanda.concepto.presentation.viewmodel.HotPointLogViewModel
import com.redpanda.concepto.presentation.viewmodel.HotPointViewModel

class MainActivity : ComponentActivity()
{
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConceptoTheme {

                val context = applicationContext

                val db = remember {
                    androidx.room.Room.databaseBuilder(
                        context,
                        AppDbContext::class.java,
                        "app_db"
                    )
                        .fallbackToDestructiveMigration(true)
                        .build()
                }

                val dao = db.hotPointDao()
                val repo = remember { HotPointRepository(dao = dao) }
                val viewModel = remember { HotPointViewModel(repo) }

                val daoLogs = db.HotPointLogDao()
                val repoLogs = remember { HotPointLogRepository(dao = daoLogs) }
                val vmLog = remember { HotPointLogViewModel(repoLogs) }

                var showLogs by remember { androidx.compose.runtime.mutableStateOf(false) }
                val sheetState = androidx.compose.material3.rememberModalBottomSheetState()

                val points = viewModel.points.value
                val logs = vmLog.logs.value

                androidx.compose.runtime.LaunchedEffect(Unit)
                {
                    viewModel.loadPoints()
                    vmLog.loadLogs()
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            vmLog.loadLogs()
                            showLogs = true
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Ver Historial de Visitas (${logs.size})")
                    }

                    AddPointForm(viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                    PointList(points, viewModel)

                    if (showLogs) {
                        androidx.compose.material3.ModalBottomSheet(
                            onDismissRequest = { showLogs = false },
                            sheetState = sheetState
                        ) {
                            LogListContent(logs)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PointList(points: List<HotPoint>, viewModel: HotPointViewModel)
{
    Column {
        points.forEach { point ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = point.description)
                    Text(
                        text = "Lat: ${point.lat}, Lon: ${point.lon}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Button(onClick = { viewModel.deletePointById(point.id) }) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun AddPointForm(viewModel: HotPointViewModel) {
    var description = remember { androidx.compose.runtime.mutableStateOf("") }
    var lat = remember { androidx.compose.runtime.mutableStateOf("") }
    var lon = remember { androidx.compose.runtime.mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material3.OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        androidx.compose.material3.OutlinedTextField(
            value = lat.value,
            onValueChange = { lat.value = it },
            label = { Text("Latitud") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Corregido: fillMaxWidth()
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
            ) // Teclado numérico con punto decimal
        )

        androidx.compose.material3.OutlinedTextField(
            value = lon.value,
            onValueChange = { lon.value = it },
            label = { Text("Longitud") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Corregido: fillMaxWidth()
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
            ) // Teclado numérico con punto decimal
        )

        val canAdd = description.value.isNotBlank() && lat.value.toDoubleOrNull() != null && lon.value.toDoubleOrNull() != null

        Button(
            onClick = {
                val latitude = lat.value.toDoubleOrNull()
                val longitude = lon.value.toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    val point = HotPoint(
                        id = 0,
                        lat = latitude,
                        lon = longitude,
                        description = description.value
                    )
                    viewModel.addPoint(point)
                    description.value = ""
                    lat.value = ""
                    lon.value = ""
                }
            },
            enabled = canAdd,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Agregar punto")
        }
    }
}

@Composable
fun LogListContent(logs: List<com.redpanda.concepto.domain.model.HotPointLog>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(400.dp) // Altura fija o usar weight
    ) {
        Text(
            text = "Puntos Visitados",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (logs.isEmpty()) {
            Text("No hay registros aún.", color = Color.Gray)
        } else {
            androidx.compose.foundation.lazy.LazyColumn {
                items(logs.size) { index ->
                    val log = logs[index]
                    val date = java.text.SimpleDateFormat("HH:mm:ss - dd/MM/yy", java.util.Locale.getDefault())
                        .format(java.util.Date(log.timestamp))

                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍", modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text(text = log.description, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Detectado el $date",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp)) // Espacio para no chocar con el borde
    }
}