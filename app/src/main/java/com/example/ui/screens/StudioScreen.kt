package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Crop169
import androidx.compose.material.icons.filled.Crop54
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.GenerationStep
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoStyle
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanLight
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VideoStudioViewModel

@Composable
fun StudioScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val promptText by viewModel.promptInput.collectAsState()
    val activeStyle by viewModel.selectedStyle.collectAsState()
    val activeRatio by viewModel.selectedAspectRatio.collectAsState()
    val sceneCount by viewModel.sceneCountChoice.collectAsState()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsState()
    val areSubtitlesEnabled by viewModel.areSubtitlesEnabled.collectAsState()
    val currentStep by viewModel.generationStep.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize().background(CosmicBlack)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // Hero Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_ai_studio_1792950925100),
                    contentDescription = "Studio IA",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CosmicBlack.copy(alpha = 0.6f),
                                    CosmicBlack
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = NeonViolet.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonVioletLight.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AUTONOME & SANS CLÉ REQUISE",
                                    color = ElectricCyanLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Générateur Vidéo Text-to-Video",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Tapez votre idée : l'IA recherche les meilleurs outils et génère le film complet.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // Prompt Section
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = DarkSurfaceElevated),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Scénario / Description",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Magic prompt enhancer button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonViolet.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.enhancePromptMagic() }
                                    .testTag("magic_enhance_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Améliorer le prompt",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Amélioration IA",
                                        color = ElectricCyanLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { viewModel.setPrompt(it) },
                            placeholder = {
                                Text(
                                    "Ex: Un train magnétique traversant une forêt de séquoias sur Mars à l'aube...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            minLines = 3,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = DarkSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("prompt_text_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Inspirations
                        Text(
                            text = "Idées rapides inspirantes :",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InspirationChip(
                                label = "⚡ Course Néo-Tokyo",
                                onClick = {
                                    viewModel.applyInspiration(
                                        "Une course effrénée de véhicules antigravité sur les toits d'une mégalopole illuminée de néons et d'hologrammes géants",
                                        VideoStyle.CYBERPUNK_2099,
                                        VideoAspectRatio.LANDSCAPE_16_9
                                    )
                                }
                            )
                            InspirationChip(
                                label = "🌌 Portail d'Andromède",
                                onClick = {
                                    viewModel.applyInspiration(
                                        "L'ouverture d'une porte des étoiles orbitale émettant des ondes gravitationnelles près d'un trou noir supermassif",
                                        VideoStyle.SCI_FI_ODYSSEY,
                                        VideoAspectRatio.PORTRAIT_9_16
                                    )
                                }
                            )
                            InspirationChip(
                                label = "🎨 Cité dans les Nuages",
                                onClick = {
                                    viewModel.applyInspiration(
                                        "Une forteresse flottante paisible avec moulins à vent et jardins suspendus peints dans le style des chefs-d'œuvre Ghibli",
                                        VideoStyle.ANIME_GHIBLI,
                                        VideoAspectRatio.LANDSCAPE_16_9
                                    )
                                }
                            )
                            InspirationChip(
                                label = "🌿 Récif Corallien 8K",
                                onClick = {
                                    viewModel.applyInspiration(
                                        "Ballet sous-marin magique de raies mantas bioluminescentes dans les abysses d'un océan cristallin",
                                        VideoStyle.NATURE_DOCUMENTARY,
                                        VideoAspectRatio.LANDSCAPE_16_9
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Styles Selection
                Text(
                    text = "Style Visuel & Direction Artistique",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Définit la colorimétrie, l'optique de caméra et l'ambiance",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VideoStyle.values()) { style ->
                        StyleCard(
                            style = style,
                            isSelected = style == activeStyle,
                            onClick = { viewModel.selectedStyle.value = style }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Format & Duration Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Aspect Ratio Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Format Vidéo",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                VideoAspectRatio.values().forEach { ratio ->
                                    val isSel = ratio == activeRatio
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSel) NeonViolet.copy(alpha = 0.35f) else DarkSurface)
                                            .border(
                                                width = if (isSel) 1.5.dp else 1.dp,
                                                color = if (isSel) ElectricCyan else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.selectedAspectRatio.value = ratio }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = when (ratio) {
                                                    VideoAspectRatio.LANDSCAPE_16_9 -> Icons.Default.Crop169
                                                    VideoAspectRatio.PORTRAIT_9_16 -> Icons.Default.Crop54
                                                    VideoAspectRatio.SQUARE_1_1 -> Icons.Default.CropSquare
                                                },
                                                contentDescription = ratio.ratioText,
                                                tint = if (isSel) ElectricCyan else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = ratio.ratioText,
                                                color = if (isSel) TextPrimary else TextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Scene count Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Durée / Plans",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf(2 to "10s", 3 to "15s", 4 to "20s").forEach { (count, duration) ->
                                    val isSel = count == sceneCount
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSel) NeonViolet.copy(alpha = 0.35f) else DarkSurface)
                                            .border(
                                                width = if (isSel) 1.5.dp else 1.dp,
                                                color = if (isSel) ElectricCyan else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { viewModel.sceneCountChoice.value = count }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${count} Plans",
                                                color = if (isSel) TextPrimary else TextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = duration,
                                                color = if (isSel) ElectricCyanLight else TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Automation Toggles Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = NeonVioletLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Voix Narration IA (TTS)",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Synthèse vocale automatique de chaque scène",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isTtsEnabled,
                                onCheckedChange = { viewModel.isTtsEnabled.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ElectricCyan,
                                    checkedTrackColor = NeonViolet
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ClosedCaption,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Sous-titres Cinématographiques",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Affichage dynamique incrusté sur la vidéo",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = areSubtitlesEnabled,
                                onCheckedChange = { viewModel.areSubtitlesEnabled.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ElectricCyan,
                                    checkedTrackColor = NeonViolet
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Generate Button
                val infiniteTransition = rememberInfiniteTransition()
                val shimmerAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Button(
                    onClick = { viewModel.generateVideoAutonomous() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonViolet, CyberPink, ElectricCyan)
                            )
                        )
                        .testTag("generate_video_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = CosmicBlack,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GÉNÉRER LA VIDÉO AUTOMATIQUEMENT",
                            color = CosmicBlack,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Automated Generation Progress Dialog
        if (currentStep !is GenerationStep.Idle) {
            GenerationProgressDialog(
                step = currentStep,
                onDismiss = { viewModel.dismissGenerationModal() }
            )
        }
    }
}

@Composable
fun InspirationChip(label: String, onClick: () -> Unit) {
    Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceVariant),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun StyleCard(
    style: VideoStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) ElectricCyan else Color.Transparent

    Card(
        modifier = Modifier
            .width(170.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurfaceElevated else DarkSurface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Style color palette circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    style.colorPalette.take(3).forEach { colorLong ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                        )
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CosmicBlack,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = style.title,
                color = if (isSelected) ElectricCyanLight else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = style.subtitle,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun GenerationProgressDialog(
    step: GenerationStep,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (step is GenerationStep.Error || step is GenerationStep.Completed) {
                onDismiss()
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonViolet.copy(alpha = 0.4f), ElectricCyan.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (step is GenerationStep.Error) {
                        Text(text = "⚠️", fontSize = 28.sp)
                    } else if (step is GenerationStep.Completed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        CircularProgressIndicator(
                            color = ElectricCyan,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = step.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = step.detail,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = { step.progress },
                    color = ElectricCyan,
                    trackColor = DarkSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Steps list visualization
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StepItemRow(1, "Analyse & Expansion du prompt", step.stepNumber >= 1)
                    StepItemRow(2, "Sélection automatique d'outil IA", step.stepNumber >= 2)
                    StepItemRow(3, "Génération du Storyboard & Plans", step.stepNumber >= 3)
                    StepItemRow(4, "Synthèse Vocale de la Narration", step.stepNumber >= 4)
                    StepItemRow(5, "Rendu des Effets & Assemblage Vidéo", step.stepNumber >= 5)
                }

                if (step is GenerationStep.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                    ) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItemRow(index: Int, label: String, isDoneOrActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isDoneOrActive) ElectricCyan else DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isDoneOrActive) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CosmicBlack,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isDoneOrActive) TextPrimary else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isDoneOrActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
