package QueenMod.powers;

import QueenMod.QueenMod;
import QueenMod.actions.AddToTopOfDrawPileAction;
import QueenMod.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static QueenMod.QueenMod.makePowerPath;

//Gain 1 dex for the turn for each card played.

public class SkillNextTurnPower extends AbstractPower implements CloneablePowerInterface {
    public AbstractCreature source;

    public static final String POWER_ID = QueenMod.makeID("SkillNextTurnPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // We create 2 new textures *Using This Specific Texture Loader* - an 84x84 image and a 32x32 one.
    // There's a fallback "missing texture" image, so the game shouldn't crash if you accidentally put a non-existent file.
    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("placeholder_power84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("placeholder_power32.png"));
    AbstractCard[] upgradeMatrix;
    CardGroup p;


    public SkillNextTurnPower(final AbstractCreature owner, final AbstractCreature source, final int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;
        this.source = source;

        type = PowerType.BUFF;
        isTurnBased = false;

        // We load those txtures here.
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        this.loadRegion("flight");

        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.player.drawPile.group.size() - 1 < this.amount) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.POWER_ID));
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new AddToTopOfDrawPileAction(AbstractDungeon.player.drawPile, this.amount, AbstractCard.CardType.SKILL));
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.POWER_ID));
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
        return new FlightDrain(owner, source, amount);
    }
}
