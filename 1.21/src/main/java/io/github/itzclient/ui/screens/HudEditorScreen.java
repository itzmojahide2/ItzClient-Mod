package io.github.itzclient.ui.screens;

import io.github.itzclient.modules.hud.HudManager;
import io.github.itzclient.modules.hud.gui.component.HudEntry;
import io.github.itzclient.modules.hud.util.DrawPosition;
import io.github.itzclient.ui.widgets.ModuleWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.Text;
import java.util.Optional;

public class HudEditorScreen extends Screen {

    private HudEntry currentEntry;
    private DrawPosition dragOffset;
    private boolean isDragging;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().margin(5);
        int columns = 0;
        int rows = 0;
        final int MAX_COLUMNS = 4;

        for (HudEntry module : HudManager.getInstance().getEntries()) {
            if (module.movable()) {
                grid.add(new ModuleWidget(module), rows, columns);
                columns++;
                if (columns >= MAX_COLUMNS) {
                    columns = 0;
                    rows++;
                }
            }
        }
        
        SimplePositioningWidget.setPos(grid, 0, 20, this.width, this.height, 0.5f, 0.2f);
        this.addDrawableChild(grid);
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> this.close())
            .position(this.width / 2 - 75, this.height - 30)
            .size(150, 20)
            .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        HudManager.getInstance().renderPlaceholder(graphics, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<HudEntry> clickedEntry = HudManager.getInstance().getEntryXY((int) mouseX, (int) mouseY);
        if (button == 0 && clickedEntry.isPresent()) {
            currentEntry = clickedEntry.get();
            dragOffset = new DrawPosition((int) (mouseX - currentEntry.getTrueX()), (int) (mouseY - currentEntry.getTrueY()));
            isDragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && currentEntry != null) {
            currentEntry.setX((int) mouseX - dragOffset.x() + currentEntry.offsetTrueWidth());
            currentEntry.setY((int) mouseY - dragOffset.y() + currentEntry.offsetTrueHeight());
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        currentEntry = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        HudManager.getInstance().saveConfig();
        this.client.setScreen(null);
    }
          }
