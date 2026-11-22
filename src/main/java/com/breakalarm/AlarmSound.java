package com.breakalarm;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/** Loopender Alarm-Sound, der sich selbst beendet, sobald die Gefahr vorbei ist. */
public class AlarmSound extends MovingSoundInstance {
	public static final SoundEvent EVENT = SoundEvent.of(Identifier.of("breakalarm", "alarm"));

	public AlarmSound() {
		super(EVENT, SoundCategory.MASTER, net.minecraft.util.math.random.Random.create());
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 1.0f;
		this.pitch = 1.0f;
		this.relative = true; // immer direkt am Ohr, egal wo du bist
		this.attenuationType = SoundInstance.AttenuationType.NONE;
	}

	@Override
	public void tick() {
		if (!BreakAlarmClient.isInDanger()) {
			this.setDone();
		}
	}
}
