package com.example.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraMovement
import com.example.data.model.ParticleEffectType
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoProject
import com.example.data.model.VideoScene
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanLight
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CinematicVideoPlayer(
    project: VideoProject,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    onSceneChanged: (VideoScene) -> Unit = {}
) {
    if (project.scenes.isEmpty()) return

    var isPlaying by remember { mutableStateOf(true) }
    var currentTimeSec by remember { mutableFloatStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }
    val totalDuration = project.totalDurationSeconds.coerceAtLeast(1f)

    // Current active scene
    val sceneDuration = 5.0f
    val currentSceneIndex = (currentTimeSec / sceneDuration).toInt().coerceIn(0, project.scenes.size - 1)
    val currentScene = project.scenes[currentSceneIndex]

    // Track active scene callback
    LaunchedEffect(currentSceneIndex) {
        onSceneChanged(currentScene)
    }

    // Playback loop using frame nanos
    LaunchedEffect(isPlaying, totalDuration) {
        if (!isPlaying) return@LaunchedEffect
        var lastNanos = 0L
        while (isPlaying) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = (frameNanos - lastNanos) / 1_000_000_000f
                    val newTime = currentTimeSec + dt
                    if (newTime >= totalDuration) {
                        currentTimeSec = 0f // Loop playback
                    } else {
                        currentTimeSec = newTime
                    }
                }
                lastNanos = frameNanos
            }
        }
    }

    val sceneLocalTime = (currentTimeSec % sceneDuration) / sceneDuration // 0f to 1f

    val aspectModifier = when (project.aspectRatio) {
        VideoAspectRatio.LANDSCAPE_16_9 -> Modifier.aspectRatio(16f / 9f)
        VideoAspectRatio.PORTRAIT_9_16 -> Modifier.aspectRatio(9f / 16f)
        VideoAspectRatio.SQUARE_1_1 -> Modifier.aspectRatio(1f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
            .background(CosmicBlack)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            },
        contentAlignment = Alignment.Center
    ) {
        // Frame Container maintaining aspect ratio
        Box(
            modifier = if (isFullscreen) Modifier.fillMaxSize() else aspectModifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Live Canvas Video Shader & Motion Engine
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("video_canvas_surface")
            ) {
                drawCinematicScene(
                    scene = currentScene,
                    sceneProgress = sceneLocalTime,
                    currentTime = currentTimeSec
                )
            }

            // Subtitles Overlay
            if (project.areSubtitlesEnabled && currentScene.narrationSnippet.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (areControlsVisible) 76.dp else 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentScene.narrationSnippet,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Top Info Bar (Scene indicator & Model badge)
            AnimatedVisibility(
                visible = areControlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = NeonViolet.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PLAN ${currentScene.sceneIndex} / ${project.scenes.size}",
                                color = NeonVioletLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentScene.cameraMovement.label,
                            color = ElectricCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("fullscreen_button")
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Plein écran",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Player Controls (Play, Seek, Time)
            AnimatedVisibility(
                visible = areControlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Scrub slider
                    Slider(
                        value = currentTimeSec,
                        onValueChange = {
                            currentTimeSec = it
                        },
                        valueRange = 0f..totalDuration,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .testTag("video_seek_bar")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    currentTimeSec = (currentTimeSec - 5f).coerceAtLeast(0f)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastRewind,
                                    contentDescription = "Reculer",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan.copy(alpha = 0.2f))
                                    .testTag("play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Lecture",
                                    tint = ElectricCyan
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentTimeSec = (currentTimeSec + 5f).coerceAtMost(totalDuration)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Avancer",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "${formatSeconds(currentTimeSec)} / ${formatSeconds(totalDuration)}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Son",
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            IconButton(
                                onClick = { currentTimeSec = 0f },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = "Recommencer",
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCinematicScene(
    scene: VideoScene,
    sceneProgress: Float,
    currentTime: Float
) {
    val width = size.width
    val height = size.height

    // 1. Ken Burns Camera Motion
    val (zoomFactor, panX, panY) = calculateCameraMotion(
        movement = scene.cameraMovement,
        progress = sceneProgress,
        width = width,
        height = height
    )

    // 2. Base Cinematic Gradient Background
    val primary = Color(scene.primaryColorHex)
    val secondary = Color(scene.secondaryColorHex)
    val accent = Color(scene.accentColorHex)

    val gradientOffset = Offset(
        x = width * 0.5f + panX * 0.5f,
        y = height * 0.5f + panY * 0.5f
    )

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(secondary, primary, CosmicBlack),
            center = gradientOffset,
            radius = (width.coerceAtLeast(height) * 0.9f) * zoomFactor
        ),
        size = size
    )

    // 3. Dynamic Horizon / Perspective Grid / Waves
    drawPerspectiveGrid(
        scene = scene,
        progress = sceneProgress,
        accent = accent,
        secondary = secondary,
        panX = panX,
        panY = panY
    )

    // 4. Focal Celestial / Geometric Centerpiece (Sun, Portal, or Monolith)
    drawCenterpiece(
        scene = scene,
        progress = sceneProgress,
        accent = accent,
        secondary = secondary,
        zoom = zoomFactor,
        panX = panX,
        panY = panY
    )

    // 5. Atmospheric Particles
    drawAtmosphericParticles(
        particleType = scene.particleType,
        progress = sceneProgress,
        currentTime = currentTime,
        seed = scene.seed,
        accent = accent
    )

    // 6. Cinematic Vignette (Dark edges)
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.68f)),
            center = Offset(width / 2f, height / 2f),
            radius = width.coerceAtLeast(height) * 0.75f
        ),
        size = size
    )

    // 7. Subtle 35mm Film Grain / Scanline feel
    val scanlineCount = (height / 8f).toInt()
    for (i in 0 until scanlineCount step 3) {
        val y = i * 8f
        drawLine(
            color = Color.Black.copy(alpha = 0.08f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
    }
}

private fun calculateCameraMotion(
    movement: CameraMovement,
    progress: Float,
    width: Float,
    height: Float
): Triple<Float, Float, Float> {
    return when (movement) {
        CameraMovement.ZOOM_IN -> Triple(1.0f + progress * 0.28f, 0f, 0f)
        CameraMovement.ZOOM_OUT -> Triple(1.28f - progress * 0.25f, 0f, 0f)
        CameraMovement.PAN_RIGHT -> Triple(1.15f, -progress * width * 0.15f, 0f)
        CameraMovement.PAN_LEFT -> Triple(1.15f, progress * width * 0.15f, 0f)
        CameraMovement.TILT_UP -> Triple(1.1f, 0f, progress * height * 0.12f)
        CameraMovement.ORBIT_SLOW -> {
            val angle = progress * PI.toFloat() * 0.5f
            Triple(1.12f, cos(angle) * width * 0.08f, sin(angle) * height * 0.05f)
        }
        CameraMovement.DRONE_SWEEP -> {
            Triple(1.05f + progress * 0.2f, (progress - 0.5f) * width * 0.12f, -progress * height * 0.1f)
        }
    }
}

private fun DrawScope.drawPerspectiveGrid(
    scene: VideoScene,
    progress: Float,
    accent: Color,
    secondary: Color,
    panX: Float,
    panY: Float
) {
    val horizonY = size.height * 0.55f + panY * 0.3f
    val centerX = size.width * 0.5f + panX * 0.3f

    // Horizon glow line
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, accent.copy(alpha = 0.8f), Color.Transparent)
        ),
        start = Offset(0f, horizonY),
        end = Offset(size.width, horizonY),
        strokeWidth = 3f
    )

    // Perspective lines radiating to horizon
    val lineCount = 12
    for (i in 0..lineCount) {
        val bottomX = (size.width / lineCount) * i
        drawLine(
            color = accent.copy(alpha = 0.25f),
            start = Offset(centerX, horizonY),
            end = Offset(bottomX, size.height),
            strokeWidth = 1.5f
        )
    }

    // Moving horizontal grid lines
    val gridStep = 5
    for (j in 1..gridStep) {
        val t = ((j.toFloat() / gridStep) + progress * 0.2f) % 1f
        val lineY = horizonY + (size.height - horizonY) * (t * t)
        drawLine(
            color = secondary.copy(alpha = 0.22f * (1f - (horizonY / lineY))),
            start = Offset(0f, lineY),
            end = Offset(size.width, lineY),
            strokeWidth = 1.2f
        )
    }
}

private fun DrawScope.drawCenterpiece(
    scene: VideoScene,
    progress: Float,
    accent: Color,
    secondary: Color,
    zoom: Float,
    panX: Float,
    panY: Float
) {
    val center = Offset(
        x = size.width * 0.5f + panX,
        y = size.height * 0.42f + panY
    )
    val baseRadius = (size.width.coerceAtMost(size.height) * 0.22f) * zoom

    // Multi-ring glowing celestial orb or portal
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accent.copy(alpha = 0.85f), secondary.copy(alpha = 0.4f), Color.Transparent),
            center = center,
            radius = baseRadius * 1.5f
        ),
        radius = baseRadius * 1.5f,
        center = center
    )

    // Central geometric core
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = baseRadius * 0.28f,
        center = center
    )

    // Orbital ring
    val ringAngle = progress * 360f
    drawCircle(
        color = accent.copy(alpha = 0.6f),
        radius = baseRadius * 0.85f,
        center = center,
        style = Stroke(width = 2.5f)
    )

    // Orbital satellites / energy nodes
    val nodeCount = 3
    for (k in 0 until nodeCount) {
        val rad = (ringAngle + k * (360f / nodeCount)) * (PI / 180f).toFloat()
        val nodePos = Offset(
            x = center.x + cos(rad) * (baseRadius * 0.85f),
            y = center.y + sin(rad) * (baseRadius * 0.85f) * 0.4f // Elliptical 3D tilt
        )
        drawCircle(
            color = Color.White,
            radius = 4f,
            center = nodePos
        )
    }
}

private fun DrawScope.drawAtmosphericParticles(
    particleType: ParticleEffectType,
    progress: Float,
    currentTime: Float,
    seed: Long,
    accent: Color
) {
    if (particleType == ParticleEffectType.NONE) return

    val random = Random(seed)
    val particleCount = when (particleType) {
        ParticleEffectType.CYBER_EMBERS -> 45
        ParticleEffectType.COSMIC_NEBULA -> 60
        ParticleEffectType.RAIN_ATMOSPHERE -> 70
        else -> 35
    }

    for (i in 0 until particleCount) {
        val initialX = random.nextFloat() * size.width
        val initialY = random.nextFloat() * size.height
        val speed = 25f + random.nextFloat() * 75f
        val radius = 1.5f + random.nextFloat() * 3.5f

        val particleColor = when (particleType) {
            ParticleEffectType.CYBER_EMBERS -> if (i % 2 == 0) Color(0xFFFFB703) else Color(0xFFFF2A85)
            ParticleEffectType.COSMIC_NEBULA -> if (i % 3 == 0) ElectricCyan else NeonVioletLight
            ParticleEffectType.RAIN_ATMOSPHERE -> Color(0xFF70FFF0).copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.7f)
        }

        when (particleType) {
            ParticleEffectType.RAIN_ATMOSPHERE -> {
                // Diagonal fast streaks
                val curY = (initialY + (currentTime * speed * 2.5f)) % size.height
                val curX = (initialX - (currentTime * speed * 0.5f)) % size.width
                drawLine(
                    color = particleColor,
                    start = Offset(curX, curY),
                    end = Offset(curX - 4f, curY + 16f),
                    strokeWidth = 1.5f
                )
            }
            ParticleEffectType.CYBER_EMBERS -> {
                // Rising turbulent embers
                val curY = (initialY - (currentTime * speed)) % size.height
                val adjustedY = if (curY < 0) curY + size.height else curY
                val curX = initialX + sin((currentTime + i) * 2f) * 15f
                drawCircle(
                    color = particleColor.copy(alpha = 0.8f),
                    radius = radius,
                    center = Offset(curX % size.width, adjustedY)
                )
            }
            else -> {
                // Floating cosmic dust
                val curY = (initialY - (currentTime * speed * 0.2f)) % size.height
                val adjustedY = if (curY < 0) curY + size.height else curY
                val curX = (initialX + cos((currentTime + i) * 0.8f) * 10f) % size.width
                drawCircle(
                    color = particleColor.copy(alpha = 0.65f),
                    radius = radius,
                    center = Offset(curX, adjustedY)
                )
            }
        }
    }
}

private fun formatSeconds(sec: Float): String {
    val totalSec = sec.toInt()
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format("%02d:%02d", minutes, seconds)
}
