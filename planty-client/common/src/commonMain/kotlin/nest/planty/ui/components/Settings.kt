package nest.planty.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nest.planty.Res
import nest.planty.ui.SmallCircularProgressIndicator

@Composable
fun SettingLabel(
    modifier: Modifier = Modifier,
    settingText: String? = null,
    settingName: String,
    settingTextStyle: TextStyle = LocalTextStyle.current,
    settingNameStyle: TextStyle = settingTextStyle.plus(TextStyle(fontWeight = FontWeight.SemiBold)),
    settingValueTextAlign: TextAlign? = null,
) {
    SettingLabel(
        modifier = modifier,
        settingIndicator = {
            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = settingText,
                label = "Setting label text loading indicator",
            ) {
                if (it != null) {
                    Text(
                        text = it,
                        style = settingTextStyle,
                        textAlign = settingValueTextAlign,
                    )
                } else {
                    SmallCircularProgressIndicator()
                }
            }
        },
        settingName = settingName,
        settingNameStyle = settingNameStyle
    )
}

@Composable
fun SettingLabel(
    modifier: Modifier = Modifier,
    settingName: String,
    settingNameStyle: TextStyle = LocalTextStyle.current.plus(TextStyle(fontWeight = FontWeight.SemiBold)),
    settingIndicator: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = settingName,
            style = settingNameStyle,
        )
        settingIndicator()
    }
}

@Composable
fun TextFieldSetting(
    value: String,
    setValue: (String) -> Unit,
    settingName: String,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.Normal,
    enabled: Boolean = true,
) {
    SettingItem(
        modifier = Modifier.fillMaxWidth(),
        settingName = settingName,
        titleStyle = textStyle,
        titleWeight = fontWeight,
        enabled = enabled,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = value,
                onValueChange = setValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
//                    style = textStyle,
//                    color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun BooleanSetting(
    value: Boolean,
    setValue: (Boolean) -> Unit,
    settingName: String,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.Normal,
    enabledText: String = Res.string.on,
    disabledText: String = Res.string.off,
    enabled: Boolean = true,
) {
    SettingItem(
        modifier = Modifier.fillMaxWidth(),
        settingName = settingName,
        onClick = { setValue(!value) },
        titleStyle = textStyle,
        titleWeight = fontWeight,
        enabled = enabled,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = value,
                label = "Boolean setting text"
            ) { enabled ->
                Text(
                    text = if (enabled) enabledText else disabledText,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = value,
                onCheckedChange = setValue,
                enabled = enabled
            )
        }
    }
}

// DropdownSetting when opened likes to shift to the start unintentionally.
// Wrap it inside a Row or layout to prevent this.
@Composable
fun <T : Any> DropdownSetting(
    selectedValue: T? = null,
    isDropdownOpen: Boolean = false,
    toggleDropdown: () -> Unit = {},
    values: Iterable<T> = emptyList(),
    getValueName: @Composable (T) -> String = { it.toString() },
    getValueLeadingIcon: (T) -> ImageVector? = { null },
    getValueTrailingIcon: (T) -> ImageVector? = { null },
    selectValue: (T) -> Unit,
    settingName: String,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    SettingItem(
        modifier = Modifier.fillMaxWidth(),
        settingName = settingName,
        onClick = toggleDropdown,
        titleStyle = textStyle,
        titleWeight = fontWeight,
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = selectedValue,
                label = "Dropdown setting text",
            ) { state ->
                state?.let {
                    Text(
                        text = getValueName(it),
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            IconToggleButton(
                checked = isDropdownOpen,
                onCheckedChange = { toggleDropdown() }
            ) {
                Icon(
                    imageVector = if (isDropdownOpen) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    },
                    contentDescription = ""
                )
            }
        }
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = toggleDropdown,
        ) {
            values.forEach { value ->
                val leadingIcon = remember { getValueLeadingIcon(value) }
                val leadingComposable = @Composable {
                    leadingIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = ""
                        )
                    }
                }
                val trailingIcon = remember {
                    val icon = getValueTrailingIcon(value)
                    if (value == selectedValue) Icons.Rounded.Check else icon
                }
                val trailingComposable = @Composable {
                    trailingIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = ""
                        )
                    }
                }
                DropdownMenuItem(
                    modifier = if (value == selectedValue) {
                        Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(elevation = 1.dp))
                    } else {
                        Modifier
                    },
                    text = { Text(text = getValueName(value)) },
                    leadingIcon = (if (leadingIcon != null) leadingComposable else null) as? @Composable (() -> Unit),
                    trailingIcon = (if (trailingIcon != null) trailingComposable else null) as? @Composable (() -> Unit),
                    onClick = { selectValue(value); toggleDropdown() },
                    colors = if (value == selectedValue) {
                        MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.primary,
                            leadingIconColor = MaterialTheme.colorScheme.primary,
                            trailingIconColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        MenuDefaults.itemColors()
                    }
                )
            }
        }
    }
}

@Composable
fun BasicSetting(
    modifier: Modifier = Modifier,
    screenName: String,
    label: String,
    onClick: () -> Unit = {}
) = BasicSetting(
    modifier = modifier,
    title = screenName,
    label = {
        TextButton(onClick = onClick) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = ""
            )
        }
    },
    onClick = onClick
)

@Composable
fun BasicSetting(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    titleWeight: FontWeight = FontWeight.Normal,
    label: @Composable RowScope.() -> Unit = {},
    onClick: () -> Unit = {}
) {
    SettingItem(
        modifier = modifier,
        onClick = onClick,
        title = {
            Text(
                modifier = Modifier.animateContentSize(),
                text = title,
                style = titleStyle,
                fontWeight = titleWeight,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        content = label,
    )
}

@Composable
fun BasicSetting(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit = {},
    label: @Composable RowScope.() -> Unit = {},
    onClick: () -> Unit = {}
) {
    SettingItem(
        modifier = modifier,
        onClick = onClick,
        title = title,
        content = label,
    )
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    settingName: String,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    titleWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {},
) = SettingItem(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    title = {
        Text(
            modifier = Modifier.animateContentSize(),
            text = settingName,
            style = titleStyle,
            fontWeight = titleWeight,
            color = MaterialTheme.colorScheme.onSurface
        )
    },
    content = content
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    title: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    Card(
        modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick,
        enabled = enabled,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            title()
            Row { content() }
        }
    }
}
