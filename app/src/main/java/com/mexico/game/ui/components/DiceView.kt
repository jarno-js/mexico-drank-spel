package com.mexico.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mexico.game.ui.theme.AccentPrimary
import com.mexico.game.ui.theme.AccentSecondary
import com.mexico.game.ui.theme.DarkSurfaceVariant

@Composable
fun DiceView(
    value: Int?,
    isRolling: Boolean = false,
    isLocked: Boolean = false,
    canLock: Boolean = false,
    onLockClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var rotation by remember { mutableFloatStateOf(0f) }

    val rotationAnimation = rememberInfiniteTransition(label = "dice_roll")
    val animatedRotation by rotationAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .then(
                if ((canLock || isLocked) && onLockClick != null) {
                    Modifier.clickable(onClick = onLockClick)
                } else Modifier
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .rotate(if (isRolling) animatedRotation else 0f)
            .background(
                color = if (isLocked) DarkSurfaceVariant else Color.White,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = if (canLock) 3.dp else if (isLocked) 3.dp else 0.dp,
                color = if (canLock) AccentPrimary else if (isLocked) AccentSecondary else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value != null && !isRolling) {
            DiceDots(value, isLocked)
        } else if (isRolling) {
            Text(
                text = "?",
                fontSize = 48.sp,
                color = Color.Black,
                style = MaterialTheme.typography.displayLarge
            )
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(AccentSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒",
                    fontSize = 12.sp
                )
            }
        }
    }

    when {
        isLocked -> {
            Text(
                text = "Tik om los te maken",
                style = MaterialTheme.typography.labelSmall,
                color = AccentSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        canLock -> {
            Text(
                text = "Tik om vast te zetten",
                style = MaterialTheme.typography.labelSmall,
                color = AccentPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DiceDots(value: Int, isLocked: Boolean = false) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
            1 -> DotLayout1(isLocked)
            2 -> DotLayout2(isLocked)
            3 -> DotLayout3(isLocked)
            4 -> DotLayout4(isLocked)
            5 -> DotLayout5(isLocked)
            6 -> DotLayout6(isLocked)
        }
    }
}

@Composable
private fun Dot(isLocked: Boolean = false) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape
            )
            .background(
                if (isLocked) Color.White else Color(0xFF1A1A1A),
                CircleShape
            )
    )
}

@Composable
private fun DotLayout1(isLocked: Boolean = false) {
    Dot(isLocked)
}

@Composable
private fun DotLayout2(isLocked: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Dot(isLocked)
        }
    }
}

@Composable
private fun DotLayout3(isLocked: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Dot(isLocked)
        }
    }
}

@Composable
private fun DotLayout4(isLocked: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Dot(isLocked)
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Dot(isLocked)
            Dot(isLocked)
        }
    }
}

@Composable
private fun DotLayout5(isLocked: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Dot(isLocked)
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Dot(isLocked)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Dot(isLocked)
            Dot(isLocked)
        }
    }
}

@Composable
private fun DotLayout6(isLocked: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Dot(isLocked)
            Dot(isLocked)
            Dot(isLocked)
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Dot(isLocked)
            Dot(isLocked)
            Dot(isLocked)
        }
    }
}
