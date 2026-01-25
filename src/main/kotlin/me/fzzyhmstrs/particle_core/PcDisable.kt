package me.fzzyhmstrs.particle_core

import com.google.gson.Gson
import me.fzzyhmstrs.particle_core.PcConfig.logger
import net.neoforged.fml.loading.FMLPaths
import java.io.File

object PcDisable {

    private val gson = Gson().newBuilder().setPrettyPrinting().setLenient().create()

    private fun getDisabledMixinsConfig(): String {
        try {
            val file = File(FMLPaths.CONFIGDIR.get().toFile(), "particle_core_disabled_optimizations_v2.json")
            return if (!file.exists()) {
                val file2 = File(FMLPaths.CONFIGDIR.get().toFile(), "particle_core_disabled_optimizations.json")
                val text = if (file2.exists()) {
                    val str2 = file2.readLines().joinToString("")
                    file2.delete()
                    val config2 = gson.fromJson(str2, DisabledOptimizations::class.java)
                    val config = DisabledOptimizations()
                    config.disableOptimizations = config2.disableOptimizations
                    gson.toJson(config)
                } else {
                    gson.toJson(DisabledOptimizations())
                }
                file.writeText(text)
                text
            } else {
                file.readLines().joinToString("")
            }
        } catch (e: Exception) {
            logger.error("Unexpected error encountered preparing disabled mixin config for Particle Core", e)
            return "{\"disableOptimizations\": []}"
        }
    }

    var disabledOptimizations = try {
        gson.fromJson(getDisabledMixinsConfig(), DisabledOptimizations::class.java)
    } catch (e: Exception) {
        logger.error("Unexpected error encountered retreiving disabled mixins for Particle Core", e)
        DisabledOptimizations()
    }

    class DisabledOptimizations {
        var _Disable_Optimizations_Options = mapOf(
            "ROTATION" to "[Impact: Medium] Disables mixins related to vertex rotation caching (ParticleManagerRotationMixin, BillboardParticleMixin)",
            "CULLING" to "[Impact: HIGH] Disables mixins related to particle culling (FrustumAccessor, ParticleAccessor, ParticleRendererFrustumMixin, WorldRendererFrustumMixin, ParticleFrustumBlacklistMixin)",
            "TYPE" to "[Impact: Low to Medium] Disables mixins related to particle disabling and reduction (ParticleManagerTypeMixin, ParticleManagerCreatorMixin, FireworksSparkParticleMixin)",
            "DECREASE" to "[Impact: Low] Disables mixins related particle settings reduction (ALL, DECREASED, MINIMAL) (ClientWorldDecreaseMixin)",
            "LIGHTMAP" to "[Impact: Medium] Disables mixins related to light map caching (ParticleManagerCachedLightMixin, ParticleBrightnessCacheMixin, ParticleCachePosMixin, ParticleRendererCachedPosMixin, ParticleRendererBrightnessTickMixin)",
            "POTION" to "[Impact: Low] Disables mixins related to potion particle disabling (LivingEntityMixin)",
            "MOVE" to "[Impact: Medium] Disables mixins related to particle movement optimization (ParticleMoveAdjustMixin, ParticleCachePosMixin, ParticleRendererCachedPosMixin)",
            "VERTEX" to "[Impact: HIGH] Disables mixins related to particle vertex drawing optimizations (BillboardParticleVertexMixin, BufferBuilderVertexMixin)",
            "COUNT" to "[Impact: Variable] Disables mixins related to max particle count setting (ParticleRendererCountMixin)",
            "ASYNC" to "[Impact: Medium] Disables asynchronous ticking of particles (ParticleManagerAsyncMixin, ParticleRendererAccessor)",
            "RENDER_DISTANCE" to "[Impact: Variable] Disables mixins that control max particle render distance (ParticleManagerRenderDistanceMixin, ParticleAccessor, ParticleRendererRenderDistanceMixin)")

        var disableOptimizations = listOf("NONE")

        fun shouldDisableMixin(className: String): Boolean {
            /*if(disableOptimizations.contains("ROTATION")) {
                if (className.endsWith("ParticleManagerRotationMixin")
                    || className.endsWith("BillboardParticleMixin"))
                {
                    println("Disabling [$className] due to 'ROTATION' key in particle core config!")
                    return true
                }
            }*/
            if(disableOptimizations.contains("TYPE")) {
                if (className.endsWith("ParticleManagerTypeMixin")
                    || className.endsWith("ParticleManagerCreatorMixin")
                    || className.endsWith("FireworksSparkParticleMixin"))
                {
                    println("Disabling [$className] due to 'TYPE' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("DECREASE")) {
                if (className.endsWith("ClientWorldDecreaseMixin"))
                {
                    println("Disabling [$className] due to 'DECREASE' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("LIGHTMAP")) {
                if (className.endsWith("ParticleManagerCachedLightMixin")
                    || className.endsWith("ParticleBrightnessCacheMixin"))
                {
                    println("Disabling [$className] due to 'LIGHTMAP' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("POTION")) {
                if (className.endsWith("LivingEntityMixin"))
                {
                    println("Disabling [$className] due to 'POTION' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("MOVE")) {
                if (className.endsWith("ParticleMoveAdjustMixin"))
                {
                    println("Disabling [$className] due to 'MOVE' key in particle core config!")
                    return true
                }
                if(disableOptimizations.contains("LIGHTMAP")) {
                    if (className.endsWith("ParticleCachePosMixin")
                        || className.endsWith("ParticleRendererCachedPosMixin"))
                    {
                        println("Disabling [$className] due to 'MOVE' and 'LIGHTMAP' key in particle core config!")
                        return true
                    }
                }
            }
            /*if(disableOptimizations.contains("VERTEX")) {
                if (className.endsWith("BufferBuilderVertexMixin")
                    || className.endsWith("BillboardParticleVertexMixin"))
                {
                    println("Disabling [$className] due to 'VERTEX' key in particle core config!")
                    return true
                }
            }*/
            if(disableOptimizations.contains("COUNT")) {
                if (className.endsWith("ParticleRendererCountMixin"))
                {
                    println("Disabling [$className] due to 'COUNT' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("ASYNC")) {
                if (className.endsWith("ParticleManagerAsyncMixin")
                    || className.endsWith("ParticleRendererAccessor"))
                {
                    println("Disabling [$className] due to 'ASYNC' key in particle core config!")
                    return true
                }
            }
            if(disableOptimizations.contains("RENDER_DISTANCE")) {
                if (className.endsWith("ParticleManagerRenderDistanceMixin")
                    || className.endsWith("ParticleAccessor"))
                {
                    println("Disabling [$className] due to 'RENDER_DISTANCE' key in particle core config!")
                    return true
                }
            }
            return false
        }
    }
}