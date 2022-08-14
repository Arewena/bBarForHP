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
    override fun onEnable() { this.server.pluginManager.registerEvents(this, this) }

    private var BossbarList = hashMapOf<Player, BossBar>()
    var task: BukkitTask? = null

    fun initi(bossBar: BossBar) {
        task = Bukkit.getScheduler().runTaskLater(this, Runnable {
            bossBar.name((Component.text("Nothing detected.")))
            bossBar.progress(0f)
        }, 100L)
    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        val basic = BossBar.bossBar(Component.text("Nothing detected."), 0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)
        BossbarList.put(e.player, basic)
        e.player.showBossBar(basic)
    }



    @EventHandler
    fun hitEntity(e: EntityDamageByEntityEvent) {
        if (e.entity is LivingEntity && e.damager is Player && e.entityType != EntityType.ARMOR_STAND) {
            task?.cancel()

            val damage = (e.damage.toFloat() * 10.0).roundToInt() / 10f
            val victimHp = if ((e.entity as LivingEntity).health.toFloat() - damage > 0.0) { ((((((e.entity as LivingEntity).health.toFloat() - damage) * 10.0).roundToInt())) / 10.0) } else { 0.0 }

            val victim =
                if (e.entity is Player) { (e.entity as Player).name }
                else { e.entity.name }

            BossbarList[(e.damager) as Player]!!.name(Component.text("$victim, hp:$victimHp, dmg:$damage"))
            BossbarList[(e.damager) as Player]!!.progress((victimHp / (e.entity as LivingEntity).maxHealth).toFloat())


            if (victimHp > 0.0) {  BossbarList[(e.damager) as Player]!!.progress(((victimHp / (e.entity as LivingEntity).maxHealth).toFloat())) }
            else {  BossbarList[(e.damager) as Player]!!.progress(0f) }
            initi(BossbarList[(e.damager) as Player]!!)

        }
    }
}


