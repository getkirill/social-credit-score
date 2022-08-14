package com.kraskaska.minecraft.plugins.socialcredit

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent

class ComponentBuilder {
    private val component = TextComponent()
    private var tempComponent: TextComponent? = null

    fun text(str: String): ComponentBuilder {
        if(tempComponent != null) component.addExtra(tempComponent)
        tempComponent = TextComponent()
        tempComponent!!.text = str
        color(ChatColor.RESET)
        italics(false)
        return this
    }

    fun color(color: ChatColor): ComponentBuilder {
        tempComponent!!.color = color
        return this
    }

    fun italics(): ComponentBuilder {
        tempComponent!!.isItalic = true
        return this
    }
    fun italics(value: Boolean): ComponentBuilder {
        tempComponent!!.isItalic = value
        return this
    }

    fun build(): TextComponent {
        if(tempComponent != null) component.addExtra(tempComponent)
        return component
    }
}