package me.fzzyhmstrs.particle_core.plugin;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;
import me.fzzyhmstrs.particle_core.PcConfig;

public class PcConditionTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return !PcConfig.INSTANCE.getImpl().shouldDisableMixin(mixinClassName);
    }
}