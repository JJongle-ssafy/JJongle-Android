package com.ssafy.jjongle.presentation.ui.component
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//
//import com.ssafy.jjongle.data.model.PlayerPosition
//
//@Composable
//fun OXGameScoreDialog(
//    playerScores: Map<Int, Int>,
//    onClose: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.7f)),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(32.dp),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "점수 결과",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // 점수 표시
//                playerScores.forEach { (playerId, score) ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "아이 $playerId",
//                            style = MaterialTheme.typography.bodyLarge,
//                            fontWeight = FontWeight.Medium
//                        )
//                        Text(
//                            text = "${score}점",
//                            style = MaterialTheme.typography.titleLarge,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = onClose,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    ),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("확인")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun OXGameAnswerDialog(
//    currentAnswer: Boolean?,
//    playerPositions: Map<Int, PlayerPosition>,
//    onClose: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.7f)),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(32.dp),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "정답은 ${if (currentAnswer == true) "O" else "X"}입니다!",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "아이들의 위치",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                playerPositions.forEach { (playerId, position) ->
//                    val isCorrect = when (currentAnswer) {
//                        true -> position.isInOArea
//                        false -> position.isInXArea
//                        null -> false
//                    }
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "아이 $playerId",
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Text(
//                            text = if (isCorrect) "정답 ✓" else "오답 ✗",
//                            style = MaterialTheme.typography.bodyLarge,
//                            fontWeight = FontWeight.Bold,
//                            color = if (isCorrect) Color.Green else Color.Red
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = onClose,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    )
//                ) {
//                    Text("다음 문제")
//                }
//            }
//        }
//    }
//}