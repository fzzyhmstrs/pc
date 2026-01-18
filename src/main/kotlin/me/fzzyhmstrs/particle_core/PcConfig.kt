package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable
import net.minecraft.util.Identifier
import net.neoforged.fml.common.Mod
import org.slf4j.LoggerFactory

@Mod("particle_core")
class ParticleCoreNeoForge() {
    init {
        PcConfig.init()
    }
}

object PcConfig {

    fun init(){}

    val logger =  LoggerFactory.getLogger("particle_core")

    internal var renderDistance = 0.0
    internal var previousViewDistance = -1

    internal val byTypeParticleReduction: Map<Identifier, Double>
        get() = impl.byTypeReductions


    var impl: PcConfigImpl = ConfigApi.registerAndLoadConfig({ PcConfigImpl() }, RegisterType.CLIENT)

    enum class PotionDisableType(val index: Int): EnumTranslatable {
        NONE(0),
        SELF(1),
        PLAYER(2),
        ALL(3);

        override fun prefix(): String {
            return "particle_core.particle_core_config"
        }
    }

    class Comment {
        var PotionParticles_Allowed_Values = mapOf(
            "NONE" to "(no particles removed)",
            "SELF" to "(only your own particles removed)",
            "PLAYER" to "(all player particles removed)",
            "ALL" to "(all mobs potion particles removed)")
        var ParticlesAllChance = "[Value between 0.0 and 1.0] Turns down particles when in ALL mode. Ex. 0.1 will make 10% of particles DECREASED. Basically a dial between ALL and DECREASED."
        var ParticlesDecreasedChance = "[Value between 0.0 and 1.0] Turns down particles when in DECREASED mode. Ex. 0.1 will make 10% of particles MINIMAL. Basically a dial between DECREASED and MINIMAL."
        var ParticlesByType = "Map of Particle Type to spawn chance. Ex. {\"minecraft:smoke\": 0.3, \"minecraft:capfire_cozy_smoke\": 0.3} (without the backslashes) will cause Smoke and Campfire particles to spawn at 30% of their normal rate. separate entries with a comma."
        var DisableParticles = "[true or false] Completely turns off all particles on the client."
        var Disable_Optimizations_Options = mapOf(
            "ROTATION" to "[Impact: Medium] Disables mixins related to vertex rotation caching (ParticleManagerRotationMixin, BillboardParticleMixin)",
            "CULLING" to "[Impact: High] Disables mixins related to particle culling (FrustumAccessor, ParticleAccessor, ParticleManagerFrustumMixin, WorldRendererFrustumMixin)",
            "TYPE" to "[Impact: Low to Medium] Disables mixins related to particle disabling and reduction (WorldRendererTypeMixin)",
            "DECREASE" to "[Impact: Low] Disables mixins related particle settings reduction (ALL, DECREASED, MINIMAL) (WorldRendererDecreaseMixin)",
            "LIGHTMAP" to "[Impact: Medium] Disables mixins related to light map caching (ParticleManagerCachedLightMixin, ParticleMixin)",
            "POTION" to "[Impact: Low] Disables mixins related to potion particle disabling (LivingEntityMixin)")
    }
}