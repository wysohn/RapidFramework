package io.github.wysohn.rapidframework3.bukkit.manager.common.message;

import io.github.wysohn.rapidframework3.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.utils.NMSWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class BukkitMessageBuilder extends MessageBuilder<ItemStack> {
    protected BukkitMessageBuilder(String str) {
        super(str);
    }

    public static BukkitMessageBuilder forBukkitMessage(String str) {
        return new BukkitMessageBuilder(str);
    }

    public BukkitMessageBuilder withHoverShowItem(ItemStack value) {
        message.resetHover();
        try {
            Class<?> NBTCompoundClass = AbstractBukkitPlugin.getNMSClass("NBTTagCompound");
            Object NBTCompound = NBTCompoundClass.newInstance();

            NMSWrapper.target(AbstractBukkitPlugin.getCraftBukkitClass("inventory.CraftItemStack"))
                    .prepare("asNMSCopy", ItemStack.class)
                    .invoke(value) // NMS ItemStack
                    .prepare("save", NBTCompoundClass)
                    .invoke(NBTCompound);

            message.setHover_ShowItem(NBTCompound.toString());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        return this;
    }
}
