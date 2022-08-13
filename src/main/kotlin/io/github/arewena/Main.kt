package io.github.arewena

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToInt



class Main : JavaPlugin(), Listener {
    private val bossBar = BossBar.bossBar(Component.text("Nothing detected."), 0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)
    var task: BukkitTask? = null
    fun initi() {
        task = Bukkit.getScheduler().runTaskLater(this, Runnable {
            bossBar.name(Component.text("Nothing detected."))
            bossBar.progress(0f)
        }, 100L)
    }

    override fun onEnable() { this.server.pluginManager.registerEvents(this, this) }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        e.player.showBossBar(bossBar)
    }

    @EventHandler
    fun hitEntity(e: EntityDamageByEntityEvent) {
        if (e.entity is LivingEntity && e.damager is Player && e.entityType != EntityType.ARMOR_STAND) {
            task?.cancel()
            var victim = ""
            if (e.entity is Player) { victim = (e.entity as Player).name }
            else { victim = e.entity.name }
            val damage = (e.damage.toFloat() * 10.0).roundToInt() / 10f
            val victimHp = if ((e.entity as LivingEntity).health.toFloat() - damage > 0.0) { ((((((e.entity as LivingEntity).health.toFloat() - damage) * 10.0).roundToInt())) / 10.0) } else { 0.0 }
            bossBar.name(Component.text("$victim, hp:$victimHp, dmg:$damage"))
            if (victimHp > 0.0) { bossBar.progress(((victimHp / (e.entity as LivingEntity).maxHealth).toFloat())) }
            else { bossBar.progress(0f) }
            initi()

        }
    }
}


