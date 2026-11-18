package dev.jan.doubleattack.mixin;

import dev.jan.doubleattack.DoubleAttackClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

	@Shadow
	protected abstract boolean startAttack();

	private boolean doubleattack$pending = false;
	private boolean doubleattack$repeating = false;

	// A real left click: queue one extra attack for the next tick
	@Inject(method = "startAttack", at = @At("HEAD"))
	private void doubleattack$onAttack(CallbackInfoReturnable<Boolean> cir) {
		if (DoubleAttackClient.enabled && !doubleattack$repeating) {
			doubleattack$pending = true;
		}
	}

	// End of the next tick: perform the extra attack
	@Inject(method = "tick", at = @At("TAIL"))
	private void doubleattack$onTick(CallbackInfo ci) {
		if (doubleattack$pending) {
			doubleattack$pending = false;
			doubleattack$repeating = true;
			try {
				startAttack();
			} finally {
				doubleattack$repeating = false;
			}
		}
	}
}
