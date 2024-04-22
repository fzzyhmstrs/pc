package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedStringMap
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.option.ParticlesMode
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
@ConvertFrom("particle_core_config_v1.json")
class PcConfigImpl: Config(Identifier("particle_core","particle_core_config"),"","") {

    var turnOffPotionParticles = ValidatedEnum(PcConfig.PotionDisableType.NONE)

    var reduceParticlesAllChance = ValidatedFloat(0f,1f,0f)

    var reduceParticlesDecreasedChance = ValidatedFloat(0f,1f,0f)

    var disableParticles = ValidatedBoolean(false)

    var reduceParticlesByType = ValidatedStringMap(mapOf(), ValidatedString(), ValidatedDouble(1.0,1.0,0.0))

    fun shouldSpawnParticle(type: ParticleType<*>): Boolean{
        val chance = PcConfig.byTypeParticleReduction[Registries.PARTICLE_TYPE.getId(type) ?: return true] ?: return true
        return PcUtils.random.nextDouble() < chance
    }

    fun getReducedParticleSpawnType(mode: ParticlesMode): ParticlesMode{
        var outMode = mode
        if (outMode == ParticlesMode.ALL){
            if (PcUtils.random.nextFloat() < reduceParticlesAllChance.get()){
                outMode = ParticlesMode.DECREASED
            }
        }
        if (outMode == ParticlesMode.DECREASED){
            if (PcUtils.random.nextFloat() < reduceParticlesDecreasedChance.get()){
                outMode = ParticlesMode.MINIMAL
            }
        }
        return outMode
    }
}