package io.github.itzclient.modules.hud.gui.hud;

import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.itzclient.bridge.render.AxoRenderContext;
import io.github.itzclient.bridge.util.AxoIdentifier;
import io.github.itzclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.itzclient.util.events.Events;
import io.github.itzclient.util.events.impl.PlayerDirectionChangeEvent;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class PlayerHud extends BoxHudEntry {

    public static final AxoIdentifier ID = AxoIdentifier.of("itzclient", "playerhud");
    
    // --- Settings for this module ---
    protected final DoubleOption rotation = new DoubleOption("rotation", 0d, 0d, 360d);
    protected final BooleanOption dynamicRotation = new BooleanOption("dynamicrotation", true);
    protected final BooleanOption autoHide = new BooleanOption("autoHide", false);

    // --- State variables ---
    @Getter
    private static boolean currentlyRendering;
    protected float lastYawOffset = 0;
    protected float yawOffset = 0;
    protected float lastYOffset = 0;
    protected float yOffset = 0;
    protected long hideTimestamp = -1;

    public PlayerHud() {
        super(62, 94, true);
        Events.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
    }

    public void onPlayerDirectionChange(PlayerDirectionChangeEvent event) {
        // Smoothly adjust the model's rotation when the player turns
        yawOffset += (event.getYaw() - event.getPrevYaw()) / 2;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        lastYawOffset = yawOffset;
        yawOffset *= 0.93f; // Dampen the rotation to make it smooth
        lastYOffset = yOffset;
        
        // Adjust model pitch for swimming/elytra flight
        if (client.br$getPlayer() != null && client.br$getPlayer().isInSwimmingPose()) {
            float rawPitch = client.br$getPlayer().isTouchingWater() ? -90.0F - client.br$getPlayer().getPitch() : -90.0F;
            float pitch = MathHelper.lerp(client.br$getPlayer().getLeaningPitch(1), 0.0F, rawPitch);
            float height = client.br$getPlayer().getHeight();
            float offset = (float) (Math.sin(Math.toRadians(pitch)) * height);
            yOffset = Math.abs(offset) + 35;
        } else if (client.br$getPlayer() != null && client.br$getPlayer().isFallFlying()) {
            float roll = (float) client.br$getPlayer().getRoll() + 1;
            float rollFactor = MathHelper.clamp(roll * roll / 100.0F, 0.0F, 1.0F);
            float pitch = rollFactor * (-90.0F - client.br$getPlayer().getPitch()) + 90;
            float height = client.br$getPlayer().getHeight();
            float offset = (float) (Math.sin(Math.toRadians(pitch)) * height) * 50;
            yOffset = 35 - offset;
            if (pitch < 0) {
                yOffset -= (float) (((1 / (1 + Math.exp(-pitch / 4))) - .5) * 20);
            }
        } else {
            yOffset *= 0.8f; // Smoothly return to normal pitch
        }
    }

    @Override
    public void renderComponent(AxoRenderContext ctx, float delta) {
        renderPlayer(ctx, false, getTrueX(), getTrueY(), delta);
    }

    @Override
    public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
        renderPlayer(ctx, true, getTrueX(), getTrueY(), 0);
    }

    protected void renderPlayer(AxoRenderContext ctx, boolean placeholder, double x, double y, float delta) {
        if (client.br$getPlayer() == null) return;

        // Auto-hide logic
        if (!placeholder && autoHide.get()) {
            if (isPerformingAction()) {
                hideTimestamp = -1; // Player is active, don't hide
            } else if (hideTimestamp == -1) {
                hideTimestamp = System.currentTimeMillis(); // Start the hide timer
            }
            if (hideTimestamp != -1 && System.currentTimeMillis() - hideTimestamp > 500) {
                return; // Hide the HUD
            }
        }

        float lerpY = lastYOffset + ((yOffset - lastYOffset) * delta);
        float scale = getScale() * 40;

        Quaternionf quaternion = Axis.Z_POSITIVE.rotationDegrees(180.0F);
        
        float deltaYaw = client.br$getPlayer().getYaw(delta);
        if (dynamicRotation.get()) {
            deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
        }
        Quaternionf quaternionf2 = new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), deltaYaw - 180 + rotation.get().floatValue());
        quaternion.mul(quaternionf2);

        float pastYaw = client.br$getPlayer().getYaw();
        float pastPrevYaw = client.br$getPlayer().prevYaw;
        
        currentlyRendering = true;
        // The InventoryScreen has a static helper method perfect for drawing entities in the GUI
        InventoryScreen.drawEntity(ctx, (float) x / getScale(), ((float) y - lerpY) / getScale(), scale, new Vector3f(), quaternion, quaternionf2, client.br$getPlayer());
        currentlyRendering = false;

        client.br$getPlayer().setYaw(pastYaw);
        client.br$getPlayer().prevYaw = pastPrevYaw;
    }

    private boolean isPerformingAction() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.isSneaking() || player.isSprinting() || player.isFallFlying() || player.getAbilities().flying
            || player.isSubmergedInWater() || player.isInSwimmingPose() || player.hasVehicle()
            || player.isUsingItem() || player.handSwinging || player.hurtTime > 0 || player.isOnFire();
    }

    @Override
    public AxoIdentifier getId() {
        return ID;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(dynamicRotation);
        options.add(rotation);
        options.add(autoHide);
        return options;
    }
}