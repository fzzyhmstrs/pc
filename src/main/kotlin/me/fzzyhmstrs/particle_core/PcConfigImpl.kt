package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.config_util.ConfigClass
import me.fzzyhmstrs.fzzy_config.interfaces.OldClass
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validated_field.list.ValidatedStringList
import me.fzzyhmstrs.fzzy_config.validated_field.map.ValidatedStringDoubleMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.option.ParticlesMode
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.function.BiPredicate

@Environment(EnvType.CLIENT)
class PcConfigImpl: OldClass<PcConfigImpl> {

    var _comments = PcConfig.Comment()

    var turnOffPotionParticles = ValidatedEnum(PcConfig.PotionDisableType.NONE, PcConfig.PotionDisableType::class.java)

    var reduceParticlesAllChance = ValidatedFloat(0f,1f,0f)

    var reduceParticlesDecreasedChance = ValidatedFloat(0f,1f,0f)

    var disableParticles = ValidatedBoolean(false)

    var reduceParticlesByType: ValidatedStringDoubleMap = ValidatedStringDoubleMap(mapOf(), BiPredicate { id, d -> Identifier.tryParse(id) != null && d >= 0.0 && d <= 1.0 }, "Invalid identifier, or chance outside of bounds [0.0, 1.0]")

    var disableOptimizations: ValidatedStringList = ValidatedStringList(listOf(),{s -> PcConfig.validOptimizationStrings.contains(s)}, "Invalid optimization-disable key, skipping! Consult the comments section for appropriate inputs")

    fun shouldDisableMixin(className: String): Boolean{
        if(disableOptimizations.contains("ROTATION")){
            if (className.endsWith("ParticleManagerRotationMixin")
                || className.endsWith("BillboardParticleMixin"))
            {
                println("Disabling [$className] due to 'ROTATION' key in particle core config!")
                return true
            }
        }
        if(disableOptimizations.contains("CULLING")){
            if (className.endsWith("FrustumAccessor")
                || className.endsWith("ParticleAccessor")
                || className.endsWith("ParticleManagerFrustumMixin")
                || className.endsWith("WorldRendererFrustumMixin"))
            {
                println("Disabling [$className] due to 'CULLING' key in particle core config!")
                return true
            }
        }
        if(disableOptimizations.contains("TYPE")){
            if (className.endsWith("WorldRendererTypeMixin"))
            {
                println("Disabling [$className] due to 'TYPE' key in particle core config!")
                return true
            }
        }
        if(disableOptimizations.contains("DECREASE")){
            if (className.endsWith("WorldRendererDecreaseMixin"))
            {
                println("Disabling [$className] due to 'DECREASE' key in particle core config!")
                return true
            }
        }
        if(disableOptimizations.contains("LIGHTMAP")){
            if (className.endsWith("ParticleManagerCachedLightMixin")
                || className.endsWith("ParticleMixin"))
            {
                println("Disabling [$className] due to 'LIGHTMAP' key in particle core config!")
                return true
            }
        }
        if(disableOptimizations.contains("POTION")){
            if (className.endsWith("LivingEntityMixin"))
            {
                println("Disabling [$className] due to 'POTION' key in particle core config!")
                return true
            }
        }
        return false
    }

    fun shouldSpawnParticle(type: ParticleType<*>): Boolean{
        val chance = PcConfig.byTypeParticleReduction[Registries.PARTICLE_TYPE.getId(type) ?: return true] ?: return true
        return PcConfig.random.nextDouble() < chance
    }

    fun getReducedParticleSpawnType(mode: ParticlesMode): ParticlesMode{
        var outMode = mode
        if (outMode == ParticlesMode.ALL){
            if (PcConfig.random.nextFloat() < reduceParticlesAllChance.get()){
                outMode = ParticlesMode.DECREASED
            }
        }
        if (outMode == ParticlesMode.DECREASED){
            if (PcConfig.random.nextFloat() < reduceParticlesDecreasedChance.get()){
                outMode = ParticlesMode.MINIMAL
            }
        }
        return outMode
    }

    override fun generateNewClass(): PcConfigImpl {
        this._comments = PcConfig.Comment()
        return this
    }

}