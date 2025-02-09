package QueenMod.powers;

import QueenMod.QueenMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SwarmPowerEnemy extends AbstractPower {
    public AbstractCreature source;
    public static final String POWER_ID = QueenMod.makeID("SwarmE");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SwarmPowerEnemy (AbstractCreature owner, AbstractCreature source, int newAmount) {
        this.name = NAME;
        this.ID = this.POWER_ID;
        this.owner = owner;
        this.source = source;
        this.amount = newAmount;
        this.type = PowerType.DEBUFF;
        this.updateDescription();
        this.loadRegion("panache");
        if (this.amount >= 999) {
            this.amount = 999;
        }

        if (this.amount < 0) {
            this.amount = 0;
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        this.canGoNegative = false;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_STRENGTH", 0.05F);
    }

    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount == 0) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.POWER_ID));
        }

        if (this.amount >= 999) {
            this.amount = 999;
        }

        if (this.amount < 0) {
            this.amount = 0;
        }
    }

    public void atStartOfTurn() {
        AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this.owner, this.owner, this.amount));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new SwarmPowerEnemy(this.owner, this.owner, this.amount-1), -1));
    }

    @Override
    public void onDeath() {
            int am = this.amount;
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SwarmPower(AbstractDungeon.player, AbstractDungeon.player, am), am));
        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner,this.source, SwarmPowerEnemy.POWER_ID));
    }
    @SpirePatch(
            clz=AbstractMonster.class,
            method="die",
            paramtypez = {boolean.class}
    )
    public static class swarmPatch  {
        @SpirePrefixPatch
        public static void die (AbstractMonster target, boolean t) {
            if (!target.isDying && !t && target.hasPower(SwarmPowerEnemy.POWER_ID)) {
                int am = target.getPower(SwarmPowerEnemy.POWER_ID).amount;
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SwarmPower(AbstractDungeon.player, AbstractDungeon.player, am), am));
            }
        }
    }
    @SpirePatch(
            clz=AbstractMonster.class,
            method="escape",
            paramtypez = {}
    )
    public static class swarmPatchEscape  {
        @SpirePrefixPatch
        public static void escape (AbstractMonster target) {
            if (!target.isEscaping && target.hasPower(SwarmPowerEnemy.POWER_ID)) {
                int am = target.getPower(SwarmPowerEnemy.POWER_ID).amount;
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SwarmPower(AbstractDungeon.player, AbstractDungeon.player, am), am));
            }
        }
    }
}