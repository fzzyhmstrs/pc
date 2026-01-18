package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.annotations.Action
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.ParticlesMode
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

@ConvertFrom("particle_core_config_v1.json")
@IgnoreVisibility
class PcConfigImpl: Config(Identifier.of("particle_core","particle_core_config"), "","") {

    var turnOffPotionParticles = ValidatedEnum(PcConfig.PotionDisableType.NONE)

    private var reduceAllChance = ValidatedFloat(0f, 1f, 0f)

    private var reduceDecreasedChance = ValidatedFloat(0f, 1f, 0f)

    var disableParticles = ValidatedBoolean(false)

    var byTypeReductions = ValidatedIdentifierMap(mapOf(), ValidatedIdentifier.ofRegistry(Identifier.of("smoke"), Registries.PARTICLE_TYPE), ValidatedDouble(1.0, 1.0, 0.0))

    @RequiresAction(Action.RESTART)
    var maxParticlesPerSheet = ValidatedInt(16384, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)

    private var particleRenderDistanceMultiplier = ValidatedDouble(1.0, 1.0, 0.1, ValidatedNumber.WidgetType.SLIDER)

    var asynchronousTicking = ValidatedBoolean()

    fun setupParticleViewDistance() {
        PcConfig.renderDistance = MathHelper.square(MinecraftClient.getInstance().options.clampedViewDistance * 16 * particleRenderDistanceMultiplier.get())
    }

    fun shouldRenderParticle(x: Double, y: Double, z: Double, pos: Vec3d): Boolean {
        return pos.squaredDistanceTo(x, y, z) <= PcConfig.renderDistance
    }

    fun shouldSpawnParticle(type: ParticleType<*>): Boolean {
        val chance = PcConfig.byTypeParticleReduction[Registries.PARTICLE_TYPE.getId(type) ?: return true] ?: return true
        return PcUtils.random.nextDouble() < chance
    }

    fun getReducedParticleSpawnType(mode: ParticlesMode): ParticlesMode {
        var outMode = mode
        if (outMode == ParticlesMode.ALL) {
            if (PcUtils.random.nextFloat() < reduceAllChance.get()) {
                outMode = ParticlesMode.DECREASED
            }
        }
        if (outMode == ParticlesMode.DECREASED) {
            if (PcUtils.random.nextFloat() < reduceDecreasedChance.get()) {
                outMode = ParticlesMode.MINIMAL
            }
        }
        return outMode
    }
}