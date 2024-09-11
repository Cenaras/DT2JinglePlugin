package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("uniquedropjingle")
public interface PluginConfig extends Config {
	@ConfigItem(
			keyName = "enableForBoss",
			name = "Bosses",
			description = "Enables jingle for drops from the 'Bosses' section of the collection log",
			position = 1
	)
	default boolean enableForBosses() {
		return true;
	}
	@ConfigItem(
			keyName = "enableForRaid",
			name = "Raids",
			description = "Enables jingle for drops from the 'Raids' section of the collection log",
			position = 2
	)
	default boolean enableForRaids() {
		return true;
	}
	@ConfigItem(
			keyName = "enableForClues",
			name = "Clues",
			description = "Enables jingle for drops from the 'Clues' section of the collection log",
			position = 3
	)
	default boolean enableForClues() {
		return false;
	}
	@ConfigItem(
			keyName = "enableForMinigames",
			name = "Minigames",
			description = "Enables jingle for drops from the 'Minigames' section of the collection log",
			position = 4
	)
	default boolean enableForMinigames() {
		return false;
	}
	@ConfigItem(
			keyName = "enableForOther",
			name = "Other",
			description = "Enables jingle for drops from the 'Other' section of the collection log",
			position = 5
	)
	default boolean enableForOther() {
		return false;
	}
}
