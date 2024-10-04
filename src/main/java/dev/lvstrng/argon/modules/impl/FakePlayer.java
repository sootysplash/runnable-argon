package dev.lvstrng.argon.modules.impl;

import dev.lvstrng.argon.event.events.PacketSendEvent;
import dev.lvstrng.argon.event.events.Render2DEvent;
import dev.lvstrng.argon.event.listeners.PacketSendListener;
import dev.lvstrng.argon.event.listeners.Render2DListener;
import dev.lvstrng.argon.modules.Category;
import dev.lvstrng.argon.modules.Module;
import dev.lvstrng.argon.modules.setting.Setting;
import dev.lvstrng.argon.modules.setting.settings.BooleanSetting;
import dev.lvstrng.argon.utils.FakePlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

//useless
public final class FakePlayer extends Module implements PacketSendListener, Render2DListener {
    private final BooleanSetting stareSetting;
    public FakePlayerEntity fakePlayerEntity;

    public FakePlayer() {
        super("Fake Player", "Spawns a fake player", 0, Category.MISC);
        this.stareSetting = new BooleanSetting("Stare", false)
                .setDescription("Makes bro stare at you");
        this.addSettings(new Setting[]{this.stareSetting});
    }

    static MinecraftClient getMinecraftClient(final FakePlayer fakePlayer) {
        return fakePlayer.mc;
    }

    @Override
    public void onEnable() {
        this.eventBus.registerPriorityListener(PacketSendListener.class, this);
        this.eventBus.registerPriorityListener(Render2DListener.class, this);
        if (this.mc.player != null) {
            this.fakePlayerEntity = new FakePlayerEntity(
                    (PlayerEntity) this.mc.player,
                    this.mc.player.getName().getString(),
                    20.0f,
                    true,
                    this.stareSetting.getValue()
            );
            this.fakePlayerEntity.method274();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.eventBus.unregister(PacketSendListener.class, this);
        this.eventBus.unregister(Render2DListener.class, this);
        if (this.fakePlayerEntity != null) {
            this.fakePlayerEntity.method275();
        }
        super.onDisable();
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        Packet packet = event.packet;
        if (packet instanceof PlayerInteractEntityC2SPacket) {
            ((PlayerInteractEntityC2SPacket) packet).handle(new FakePlayerAttackHandler(this));
        }
    }

    @Override
    public void onRender2D(final Render2DEvent event) {
        if (this.stareSetting.getValue() && this.fakePlayerEntity != null) {
            this.fakePlayerEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, this.mc.player.getLerpedPos(this.mc.getTickDelta()));
        }
    }
}