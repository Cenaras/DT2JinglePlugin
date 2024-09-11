package com.example;


import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@PluginDescriptor(
        name = "Unique Drop Jingle Plugin"
)
public class UniqueDropJinglePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private PluginConfig config;

    @Inject
    ItemManager itemManager;

    @Inject
    private ClientThread clientThread;

    private List<Integer> uniqueItemIds;

    private static final int COLLECTION_LOG_PAGE_ITEMS_ENUM_PARAM_ID = 690;
    private static final int COLLECTION_LOG_TAB_ENUM_PARAM_ID = 683;

    private static final int BOSS_STRUCT_ID = 471;
    private static final int RAIDS_STRUCT_ID = 472;
    private static final int CLUES_STRUCT_ID = 473;
    private static final int MINIGAMES_STRUCT_ID = 474;
    private static final int OTHER_STRUCT_ID = 475;

    private static final ArrayList<Integer> COLLECTION_LOG_TAB_STRUCT_IDS = new ArrayList<>();
    private final Map<String, Integer> eventKey2StructId = Map.of(
            "enableForBoss", BOSS_STRUCT_ID,
            "enableForRaids", RAIDS_STRUCT_ID,
            "enableForClues", CLUES_STRUCT_ID,
            "enableForMinigames", MINIGAMES_STRUCT_ID,
            "enableForOther", OTHER_STRUCT_ID);

    @Override
    protected void startUp() {
        // Enable Boss and Raids by default
        COLLECTION_LOG_TAB_STRUCT_IDS.add(BOSS_STRUCT_ID);
        COLLECTION_LOG_TAB_STRUCT_IDS.add(RAIDS_STRUCT_ID);
        updateUniqueItemIds();
    }


    // TODO: Things like purchasing, Gauntlet Chest, Raids Chests etc comes from where?
    // TODO: Update list when config is updated
    @Subscribe
    public void onLootReceived(LootReceived event) {
        for (ItemStack itemStack : event.getItems()) {
            if (this.uniqueItemIds.contains(itemStack.getId())) {
                playJingle();
                return;
            }
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("uniquedropjingle")) {
            int structId = eventKey2StructId.get(event.getKey());
            boolean newVal = Boolean.parseBoolean(event.getNewValue());
            addOrRemove(structId, newVal);
            updateUniqueItemIds();

        }
    }

    private void updateUniqueItemIds() {
        clientThread.invokeLater(() -> {
            this.uniqueItemIds = getCollectionLogUniqueItemIDs();
        });
    }

    private void addOrRemove(Integer value, boolean shouldContain) {
        if (shouldContain) {
            System.out.println("Adding " + value);
            UniqueDropJinglePlugin.COLLECTION_LOG_TAB_STRUCT_IDS.add(value);
        } else {
            System.out.println("Removing " + value);
            UniqueDropJinglePlugin.COLLECTION_LOG_TAB_STRUCT_IDS.remove(value);
        }
    }


    private List<Integer> getCollectionLogUniqueItemIDs() {
        ArrayList<Integer> itemIds = new ArrayList<>();

        // Iterate all tabs in collection log
        for (int structId : COLLECTION_LOG_TAB_STRUCT_IDS) {

            // Collect list of collection pages in the current collection log tab
            StructComposition tabStruct = client.getStructComposition(structId);
            int tabEnumId = tabStruct.getIntValue(COLLECTION_LOG_TAB_ENUM_PARAM_ID);
            EnumComposition tabEnum = client.getEnum(tabEnumId);

            // Iterate pages
            for (int pageStructId : tabEnum.getIntVals()) {

                // pageStruct represents information about the current page
                StructComposition pageStruct = client.getStructComposition(pageStructId);

                // Retrieve the enum param for items in collection log page
                int pageItemsId = pageStruct.getIntValue(COLLECTION_LOG_PAGE_ITEMS_ENUM_PARAM_ID);
                EnumComposition itemsEnum = client.getEnum(pageItemsId);

                // Iterate items and collect via ItemManager
                for (int itemsEnumId : itemsEnum.getIntVals()) {
                    ItemComposition itemComposition = itemManager.getItemComposition(itemsEnumId);
                    itemIds.add(itemComposition.getId());
                }
            }
        }
        return itemIds;
    }

    private void playJingle() {
        int soundId = 6765;
        client.playSoundEffect(soundId);
    }

    @Provides
    PluginConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PluginConfig.class);
    }
}
