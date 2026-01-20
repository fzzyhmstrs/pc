package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable
import me.fzzyhmstrs.particle_core.mixins.FrustumAccessor
import me.fzzyhmstrs.particle_core.mixins.ParticleAccessor
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.Particle
import net.minecraft.client.render.Frustum
import net.minecraft.util.math.Vec3d
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Predicate

@Environment(EnvType.CLIENT)
object PcConfig: ClientModInitializer {

    val logger: Logger =  LoggerFactory.getLogger("particle_core")

    internal var renderDistance = 0.0

    internal var previousChoices = listOf<PotionDisableType>()

    internal var choiceFlags = 0

    var impl: PcConfigImpl = ConfigApi.registerAndLoadConfig({ PcConfigImpl() }, RegisterType.CLIENT)

    fun shouldRenderParticle(x: Double, y: Double, z: Double, pos: Vec3d): Boolean {
        return pos.squaredDistanceTo(x, y, z) <= renderDistance
    }

    fun shouldDisablePotionParticle(type: PotionDisableType): Boolean {
        return choiceFlags and type.flags == type.flags
    }

    enum class PotionDisableType(val flags: Int, val choicePredicate: Predicate<PotionDisableType>): EnumTranslatable {
        NONE(1, { it == NONE }),
        OTHER_PLAYER(2, { it != NONE && it != ALL }),
        SELF(4, { it != NONE && it != ALL }),
        MOBS(8, { it != NONE && it != ALL }),
        ALL(16, { it == ALL });

        override fun prefix(): String {
            return "particle_core.particle_core_config"
        }
    }

    enum class DeprecatedType(vararg val mapTo: PotionDisableType): EnumTranslatable {
        NONE(PotionDisableType.NONE),
        OTHER_PLAYER(PotionDisableType.OTHER_PLAYER),
        SELF(PotionDisableType.SELF),
        PLAYER(PotionDisableType.OTHER_PLAYER, PotionDisableType.SELF),
        MOBS(PotionDisableType.MOBS),
        ALL(PotionDisableType.ALL);

        override fun prefix(): String {
            return "particle_core.particle_core_config"
        }
    }

    enum class CullingBehavior: EnumTranslatable {
        NO_CULLING {
            override fun shouldKeep(frustum: Frustum, particle: Particle): Boolean {
                return false
            }
        },
        AGGRESSIVE {
            override fun shouldKeep(frustum: Frustum, particle: Particle): Boolean {
                return (frustum as FrustumAccessor).frustumIntersection.testPoint(
                    ((particle as ParticleAccessor).x - frustum.getX()).toFloat(),
                    ((particle as ParticleAccessor).y - frustum.getY()).toFloat(),
                    ((particle as ParticleAccessor).z - frustum.getZ()).toFloat()
                )
            }
        },
        BOUNDING_BOX {
            override fun shouldKeep(frustum: Frustum, particle: Particle): Boolean {
                return frustum.isVisible(particle.boundingBox)
            }
        };

        abstract fun shouldKeep(frustum: Frustum, particle: Particle): Boolean

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

    override fun onInitializeClient() {
    }
}