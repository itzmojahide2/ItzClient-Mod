package io.github.itzclient.util;

import com.google.common.base.Preconditions;
import io.github.itzclient.bridge.AxoMinecraftClient; // RENAMED
import io.github.itzclient.bridge.Platform;
import io.github.itzclient.bridge.entity.AxoPlayer;
import io.github.itzclient.bridge.item.AxoItem;
import io.github.itzclient.bridge.item.AxoItemStack;
import io.github.itzclient.bridge.item.AxoPlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemUtil {

    public static int getTotal(AxoMinecraftClient client, AxoItem item) {
        AxoPlayer player = client.br$getPlayer();
        if (player == null) {
            return 0;
        }
        return getTotal(player.br$getInventory(), item);
    }

    public static int getTotal(AxoPlayerInventory inventory, AxoItem item) {
        return inventory.br$getItems().stream()
            .filter(x -> x.br$getItem() == item)
            .mapToInt(AxoItemStack::br$getCount)
            .sum();
    }
    
    // ... (The rest of the class logic remains the same, but it lives in the new package)
    
    public static Optional<ItemStorage> getItemFromItem(AxoItemStack item, List<ItemStorage> list) {
        AxoItemStack compare = item.br$copy();
        compare.br$setCount(1);
        for (ItemUtil.ItemStorage storage : list) {
            if (isEqual(storage.stack, compare)) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }
    
    private static boolean isEqual(AxoItemStack stack, AxoItemStack compare) {
        return stack != null && compare != null && stack.br$getItem() == compare.br$getItem();
    }

    // ... (All other helper methods and inner classes like ItemStorage and TimedItemStorage)
    public static class ItemStorage {
        // ...
    }
    public static class TimedItemStorage extends ItemStorage {
        // ...
    }
}
