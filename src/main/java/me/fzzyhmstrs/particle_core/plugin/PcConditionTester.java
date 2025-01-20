package me.fzzyhmstrs.particle_core.plugin;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;
import me.fzzyhmstrs.particle_core.PcDisable;

public class PcConditionTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return !PcDisable.INSTANCE.getDisabledOptimizations().shouldDisableMixin(mixinClassName);
    }
}