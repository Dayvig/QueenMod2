//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package QueenMod.actions;

import QueenMod.cards.*;
import QueenMod.powers.SwarmPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.Iterator;

public class ExhaustAllBeesAction extends AbstractGameAction {
    private float startingDuration;
    public int bee;

    public ExhaustAllBeesAction(int b) {
        this.actionType = ActionType.WAIT;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
        bee = b;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            Iterator var1 = AbstractDungeon.player.hand.group.iterator();
            while(var1.hasNext()) {
                AbstractCard c = (AbstractCard)var1.next();
                System.out.println(c.cardID);
                if (c.cardID.equals(Hornet.ID) ||
                        c.cardID.equals(BumbleBee.ID) ||
                        c.cardID.equals(Drone.ID) ||
                        c.cardID.equals(HoneyBee.ID) ||
                        c.cardID.equals(HornetCommander.ID) ||
                        c.cardID.equals(BumbleBeeCommander.ID) ||
                        c.cardID.equals(DroneCommander.ID) ||
                        c.cardID.equals(HoneyBeeCommander.ID)) {
                    AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                            new SwarmPower(AbstractDungeon.player, AbstractDungeon.player, this.bee), this.bee));
                }
            }

            this.isDone = true;
            if (AbstractDungeon.player.exhaustPile.size() >= 20) {
                UnlockTracker.unlockAchievement("THE_PACT");
            }
        }

    }
}
