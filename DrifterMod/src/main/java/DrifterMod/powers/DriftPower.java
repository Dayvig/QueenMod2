package DrifterMod.powers;

import DrifterMod.DrifterMod;
import DrifterMod.actions.EurobeatAction;
import DrifterMod.characters.TheDrifter;
import DrifterMod.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

import static DrifterMod.DrifterMod.makePowerPath;

//Gain 1 dex for the turn for each card played.

public class DriftPower extends AbstractPower implements CloneablePowerInterface {
    public AbstractCreature source;

    public static final String POWER_ID = DrifterMod.makeID("DriftPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.
    // There's a fallback "missing texture" image, so the game shouldn't crash if you accidentally put a non-existent file.
    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));
    public int onlyOnce;

    public DriftPower(final AbstractCreature owner, final AbstractCreature source, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.source = source;

        type = PowerType.BUFF;

        // We load those txtures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        this.loadRegion("flex");

        updateDescription();
    }

    public DriftPower(final AbstractCreature owner, final AbstractCreature source, final int amount, int k) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.source = source;

        type = PowerType.BUFF;
        onlyOnce = k;
        // We load those txtures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        this.loadRegion("flex");

        updateDescription();
    }

    @Override
    public void atEndOfTurn(final boolean isPlayer) {
        if (!this.owner.equals(AbstractDungeon.player)){ return; }
        if (this.owner.hasPower(DriftingPower.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, DriftingPower.POWER_ID));
        }
        else {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, DriftPower.POWER_ID));
        }
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this.owner, this.owner, this.amount));
        ArrayList<AbstractMonster> m = AbstractDungeon.getCurrRoom().monsters.monsters;
        int[] tmp = new int[m.size()];
        int i;
        for(i = 0; i < tmp.length; ++i) {
            tmp[i] = this.amount;
        }
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(this.owner, tmp,
                DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));
    }

    @Override
    public void onInitialApplication() {
        switch (TheDrifter.r){
            case 0:
                AbstractDungeon.actionManager.addToBottom(new EurobeatAction("Gas"));
                return;
            case 1:
                AbstractDungeon.actionManager.addToBottom(new EurobeatAction("NightFire"));
                return;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new EurobeatAction("Dejavu"));
                return;
            default:
        }
    }

    @Override
    public void onRemove(){
        if (this.owner.hasPower(DriftStrengthDownPower.POWER_ID)){
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.owner.getPower(DriftStrengthDownPower.POWER_ID).amount), -this.owner.getPower(DriftStrengthDownPower.POWER_ID).amount));
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, DriftStrengthDownPower.POWER_ID));
        }
        CardCrawlGame.music.fadeOutTempBGM();
        CardCrawlGame.music.unsilenceBGM();
    }

    public void onCombatEnd(){

    }

    // Update the description when you apply this power. (i.e. add or remove an "s" in keyword(s))
    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        } else if (amount > 1) {
            description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new DriftPower(owner, source, amount);
    }
}
