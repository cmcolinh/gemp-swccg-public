package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Kirdo III Operative
 */
public class Card7_027 extends AbstractAlien {
    public Card7_027() {
        super(Side.LIGHT, 3, 1, 1, 1, 3, "Kirdo III Operative", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Some traders who run convoys to Kirdo III relay secret information from the Rebel population of Kirdo III. These traders have extensive experience with the Kitonak.");
        setGameText("While at a Kirdo III site: adds 1 to your Force drains there, is forfeit +1 (and power +1 if your Kitonak is on Kirdo III) and, once during each of your deploy phases, may deploy one site to Kirdo III from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.OPERATIVE);
        setMatchingSystem(Title.Kirdo_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atKirdoIIISiteCondition = new AtCondition(self, Filters.Kirdo_III_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), atKirdoIIISiteCondition, 1, playerId));
        modifiers.add(new ForfeitModifier(self, atKirdoIIISiteCondition, 1));
        modifiers.add(new PowerModifier(self, new AndCondition(atKirdoIIISiteCondition,
                new OnCondition(self, Filters.and(Filters.your(self), Filters.Kitonak), Title.Kirdo_III)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KIRDO_III_OPERATIVE__DOWNLOAD_SITE_TO_KIRDO_III;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Kirdo_III_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a site to Kirdo III from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, Title.Kirdo_III, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
