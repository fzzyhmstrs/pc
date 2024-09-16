package me.fzzyhmstrs.particle_core

import com.google.gson.Gson
import net.minecraftforge.fml.loading.FMLPaths
import java.io.File

object PcDisable {

    private val gson = Gson().newBuilder().setPrettyPrinting().setLenient().create()

    private fun getDisabledMixinsConfig(): String {
        val file = File(FMLPaths.CONFIGDIR.get().toFile(), "particle_core_disabled_optimizations.json")
        return if (!file.exists()){
            val text = gson.toJson(DisabledOptimizations())
            file.writeText(text)
            text
        } else {
            file.readLines().joinToString("")
        }
    }

    var disabledOptimizations = try{ gson.fromJson(getDisabledMixinsConfig(),DisabledOptimizations::class.java) } catch (e: Exception){ e.printStackTrace(); DisabledOptimizations() }

    class DisabledOptimizations{
        var _Disable_Optimizations_Options = mapOf(
            "ROTATION" to "[Impact: Medium] Disables mixins related to vertex rotation caching (ParticleManagerRotationMixin, BillboardParticleMixin)",
            "TYPE" to "[Impact: Low to Medium] Disables mixins related to particle disabling and reduction (WorldRendererTypeMixin)",
            "DECREASE" to "[Impact: Low] Disables mixins related particle settings reduction (ALL, DECREASED, MINIMAL) (WorldRendererDecreaseMixin)",
            "LIGHTMAP" to "[Impact: Medium] Disables mixins related to light map caching (ParticleManagerCachedLightMixin, ParticleMixin)",
            "POTION" to "[Impact: Low] Disables mixins related to potion particle disabling (LivingEntityMixin)")

        var disableOptimizations = listOf<String>("NONE")

        fun shouldDisableMixin(className: String): Boolean{
            if(disableOptimizations.contains("ROTATION")){
                if (className.endsWith("ParticleManagerRotationMixin")
                    || className.endsWith("BillboardParticleMixin"))
                {
                    println("Disabling [$className] due to 'ROTATION' key in particle core config!")
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
    }
}