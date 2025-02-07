package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: Fenn Rau
 */
public class Card219_035 extends AbstractAlienRebel {
    public Card219_035() {
        super(Side.LIGHT, 2, 4, 4, 3, 5, "Fenn Rau", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setArmor(5);
        setLore("Mandalorian scout.");
        setGameText("[Pilot] 3. During battle, if another Mandalorian here, opponent's total power is -2. Once per game, at the end of a battle here, " +
                "may return Fenn Rau to hand to activate 2 Force (if Fenn Rau won a battle this turn, may also retrieve 1 Force).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.MANDALORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new InBattleWithCondition(self, Filters.Mandalorian), -2, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FENN_RAU__RETURN_TO_HAND;
        // Check condition(s)
        if (TriggerConditions.battleEndingAt(game, effectResult, Filters.here(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canActivateForce(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 2 Force");
            action.setActionMsg("Return to hand to activate 2 Force.");
            action.appendUsage(
                new OncePerGameEffect(action));
            action.appendCost(
                    new ReturnCardToHandFromTableEffect(action, self));
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 2));
            if (GameConditions.cardHasWhileInPlayDataEquals(self, true)
                    && (GameConditions.numCardsInReserveDeck(game, playerId) >= 2)) {
                action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Retrieve 1 Force?") {
                    @Override
                    protected void yes() {
                        action.appendEffect(
                                new RetrieveForceEffect(action, playerId, 1));
                    }
                    @Override
                    protected void no() {
                        action.appendEffect(
                                new SendMessageEffect(action, playerId + " chooses not to retrieve 1 Force"));
                    }
                }));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Track if he won a battle this turn
        if (TriggerConditions.wonBattle(game, effectResult, self)) {
            self.setWhileInPlayData(new WhileInPlayData(true));
        }

        // Reset at the end of each turn
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return actions;
    }
}