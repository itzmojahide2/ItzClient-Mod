/*
 * Copyright © 2025 itzmojahide2 & Contributors
 *
 * This file is part of ItzClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */
package io.github.itzclient.modules.hud.gui.hud.vanilla;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.util.Color;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.itzclient.bridge.events.Events;
import io.github.itzclient.bridge.events.types.ScoreboardRenderEvent;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.scores.AxoScoreboardScore;
import io.github.itzclient.bridge.scores.AxoTeam;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.itzclient.modules.hud.gui.entry.TextHudEntry;
import io.github.itzclient.modules.hud.gui.layout.AnchorPoint;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.util.ClientColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHud extends TextHudEntry implements DynamicallyPositionable {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "scoreboardhud");
    
    // Create a static placeholder scoreboard for the HUD editor
    private static final ScoreboardObjective PLACEHOLDER = Util.make(() -> {
        Scoreboard board = new Scoreboard();
        ScoreboardObjective obj = board.addObjective("placeholder", ScoreboardCriterion.DUMMY, Text.literal("§e§lITZCLIENT"), ScoreboardCriterion.RenderType.INTEGER);
        board.getOrCreateScore(Text.literal("§7itzclient.net"), obj).setScore(1);
        board.getOrCreateScore(Text.literal(" "), obj).setScore(2);
        board.getOrCreateScore(Text.literal("Player: §aItzPlayer"), obj).setScore(3);
        board.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, obj);
        return obj;
    });

    // --- Settings for this module ---
    private final ColorOption topColor = new ColorOption("topbackgroundcolor", new Color(0x66000000));
    private final IntegerOption topPadding = new IntegerOption("toppadding", 0, 0, 4);
    private final BooleanOption showScores = new BooleanOption("scores", true);
    private final ColorOption scoreColor = new ColorOption("scorecolor", new Color(0xFFFF5555));
    private final IntegerOption textAlpha = new IntegerOption("text_alpha", 255, 0, 255);
    private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class, AnchorPoint.MIDDLE_RIGHT);

    public ScoreboardHud() {
        super(200, 146, true);
    }

    @Override
    public void renderComponent(AxoRenderContext context, float delta) {
        if (client.br$getWorld() == null) {
            renderPlaceholderComponent(context, delta);
            return;
        }
        Scoreboard scoreboard = client.br$getWorld().getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        
        if (objective != null) {
            // Cancel the vanilla scoreboard from rendering
            Events.SCOREBOARD_RENDER_EVENT.invoker().accept(new ScoreboardRenderEvent(objective, true));
            renderScoreboardSidebar(context, objective);
        }
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
        renderScoreboardSidebar(context, PLACEHOLDER);
    }

    private void renderScoreboardSidebar(AxoRenderContext context, ScoreboardObjective objective) {
        TextRenderer font = client.br$getFont();
        Scoreboard scoreboard = objective.getScoreboard();

        Collection<AxoScoreboardScore> scores = scoreboard.br$getScores(objective);
        List<AxoScoreboardScore> filteredScores = scores.stream()
            .filter(score -> !score.br$isHidden())
            .sorted(Comparator.comparingInt(AxoScoreboardScore::br$getScore))
            .limit(15L)
            .collect(Collectors.toList());

        Text title = objective.getDisplayName();
        int titleWidth = font.br$getWidth(title);
        int maxWidth = titleWidth;

        for (AxoScoreboardScore score : filteredScores) {
            AxoTeam team = scoreboard.br$getTeam(score.br$getOwner());
            String name = AxoTeam.br$getMemberDisplayName(team, score.br$getOwner());
            String scoreText = " " + score.br$getScore();
            int lineWidth = font.br$getWidth(name);
            if (showScores.get()) {
                lineWidth += font.br$getWidth(scoreText);
            }
            maxWidth = Math.max(maxWidth, lineWidth);
        }

        int newWidth = maxWidth + 6;
        int newHeight = (filteredScores.size() * font.br$getFontHeight()) + font.br$getFontHeight() + 5 + topPadding.get() * 2;
        
        if (getWidth() != newWidth || getHeight() != newHeight) {
            setWidth(newWidth);
            setHeight(newHeight);
            onBoundsUpdate();
        }

        DrawPosition pos = getPos();
        int xStart = pos.x() + 2;
        int yStart = pos.y() + 2;
        int xEnd = pos.x() + getWidth() - 2;

        // Draw title
        context.br$drawCenteredString(title, pos.x() + getWidth() / 2, yStart + topPadding.get(), textColor.get().toInt(), shadow.get());
        
        // Draw scores
        int y = yStart + font.br$getFontHeight() + 2 + topPadding.get() * 2;
        for (AxoScoreboardScore score : filteredScores) {
            AxoTeam team = scoreboard.br$getTeam(score.br$getOwner());
            String name = AxoTeam.br$getMemberDisplayName(team, score.br$getOwner());
            context.br$drawString(name, xStart, y, textColor.get().toInt(), shadow.get());
            if (showScores.get()) {
                String scoreText = "" + score.br$getScore();
                context.br$drawString(scoreText, xEnd - font.br$getWidth(scoreText), y, scoreColor.get().toInt(), shadow.get());
            }
            y += font.br$getFontHeight();
        }
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(topColor);
        options.add(showScores);
        options.add(scoreColor);
        options.add(anchor);
        options.add(topPadding);
        options.add(textAlpha);
        return options;
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.get();
    }
}