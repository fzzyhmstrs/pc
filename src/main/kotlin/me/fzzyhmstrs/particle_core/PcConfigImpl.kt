package me.fzzyhmstrs.particle_core

import com.google.common.collect.Sets
import me.fzzyhmstrs.fzzy_config.annotations.*
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.entry.EntryFlag
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.withListener
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber.Companion.withIncrement
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ParticleStatus
import net.minecraft.util.Mth
import java.util.function.Predicate

@Environment(EnvType.CLIENT)
@ConvertFrom("particle_core_config_v1.json")
@IgnoreVisibility
@Version(1)
class PcConfigImpl: Config(Identifier.fromNamespaceAndPath("particle_core","particle_core_config"), "","") {

    override fun update(deserializedVersion: Int) {
        if (deserializedVersion == 0) {
            val l = turnOffPotionParticles.get().mapTo.toList()
            turnOffPotionParticlesV2.validateAndSetFlagged(l, EntryFlag.Flag.QUIET)
            PcConfig.previousChoices = l
        }
    }

    @Deprecated("No longer used")
    @ConfigDeprecated
    private var turnOffPotionParticles = ValidatedEnum(PcConfig.DeprecatedType.NONE)

    var disableParticles = ValidatedBoolean(false)

    private var byTypeReductions = ValidatedIdentifierMap(mapOf(), ValidatedIdentifier.ofRegistry(Identifier.parse("smoke"), BuiltInRegistries.PARTICLE_TYPE), ValidatedDouble(1.0, 1.0, 0.0))

    @RequiresAction(Action.RESTART)
    var maxParticlesPerSheet = ValidatedInt(16384, Int.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS).withIncrement(1000)

    private var particleRenderDistanceMultiplier = ValidatedDouble(1.0, 1.0, 0.1, ValidatedNumber.WidgetType.SLIDER)

    var asynchronousTicking = ValidatedBoolean()

    private var cullingBlacklist = ValidatedIdentifier.ofRegistry(Identifier.parse("smoke"), BuiltInRegistries.PARTICLE_TYPE).toSet()

    private var cullingBehavior = ValidatedEnum(PcConfig.CullingBehavior.AGGRESSIVE)

    private var reduceAllChance = ValidatedFloat(0f, 1f, 0f)

    private var reduceDecreasedChance = ValidatedFloat(0f, 1f, 0f)

    private var turnOffPotionParticlesV2 = ValidatedEnum(PcConfig.PotionDisableType.NONE)
        .toList(PcConfig.PotionDisableType.entries)
        .toChoiceList(listOf(PcConfig.PotionDisableType.NONE), ValidatedChoiceList.WidgetType.INLINE)
        .withListener { choices ->
            val filter: Predicate<PcConfig.PotionDisableType> = Sets.difference(choices.toMutableSet(), PcConfig.previousChoices.toMutableSet()).fold(Predicate { true }) { p, c ->
                p.and(c.choicePredicate)
            }
            var actualChoices = choices.stream().filter(filter).toList()
            if (actualChoices.isEmpty()) actualChoices = listOf(PcConfig.PotionDisableType.NONE)
            if (actualChoices != choices.get()) {
                choices.validateAndSetFlagged(actualChoices, EntryFlag.Flag.QUIET)
            }
            PcConfig.previousChoices = ArrayList(choices.get())
            var flags = 0
            for (choice in choices) {
                flags = choice.flags or flags
            }
            PcConfig.choiceFlags = flags
        }

    fun setupParticleViewDistance() {
        PcConfig.renderDistance = Mth.square(Minecraft.getInstance().options.effectiveRenderDistance * 16 * particleRenderDistanceMultiplier.get())
    }

    fun shouldSpawnParticle(type: ParticleType<*>): Boolean {
        val chance = byTypeReductions[BuiltInRegistries.PARTICLE_TYPE.getKey(type) ?: return true] ?: return true
        return PcUtils.random.nextDouble() < chance
    }

    fun passParticleCull(): Boolean {
        return cullingBehavior.get().pass()
    }

    fun keepParticle(frustum: Frustum, particle: Particle): Boolean {
        return cullingBehavior.get().shouldKeep(frustum, particle)
    }

    fun shouldBlacklistParticle(type: ParticleType<*>): Boolean {
        return cullingBlacklist.contains(BuiltInRegistries.PARTICLE_TYPE.getKey(type) ?: return false)
    }

    fun getReducedParticleSpawnType(mode: ParticleStatus): ParticleStatus {
        var outMode = mode
        if (outMode == ParticleStatus.ALL){
            if (PcUtils.random.nextFloat() < reduceAllChance.get()){
                outMode = ParticleStatus.DECREASED
            }
        }
        if (outMode == ParticleStatus.DECREASED){
            if (PcUtils.random.nextFloat() < reduceDecreasedChance.get()){
                outMode = ParticleStatus.MINIMAL
            }
        }
        return outMode
    }
}