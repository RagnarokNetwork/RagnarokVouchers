package net.ragnaroknetwork.vouchers;


import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class RItemStack {
    private static final String PUBLIC_BUKKIT_VALUES_VOUCHER_ID = "Ragnarok-Voucher-Id";

    private final ItemStack nmsItemStack;

    private RItemStack(org.bukkit.inventory.ItemStack itemStack) {
        this.nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
    }

    public static RItemStack of(org.bukkit.inventory.ItemStack itemStack) {
        return new RItemStack(itemStack);
    }

    public boolean hasTag(String key) {
        return this.nmsItemStack.hasTag() && this.nmsItemStack.getTag().hasKey(key);
    }

    public NBTBase getTag(String key) {
        NBTTagCompound tag = this.nmsItemStack.hasTag() ? this.nmsItemStack.getTag() : new NBTTagCompound();
        return tag.hasKey(key) ? tag.get(key) : new NBTTagByte((byte) 0);
    }

    public RItemStack setTag(String key, NBTBase nbt) {
        NBTTagCompound tag = this.nmsItemStack.hasTag() ? this.nmsItemStack.getTag() : new NBTTagCompound();
        tag.set(key, nbt);
        this.nmsItemStack.setTag(tag);
        return this;
    }

    public org.bukkit.inventory.ItemStack toBukkitCopy() {
        return CraftItemStack.asBukkitCopy(this.nmsItemStack);
    }

    // Voucher Methods

    public boolean isVoucher() {
        return hasTag(PUBLIC_BUKKIT_VALUES_VOUCHER_ID);
    }

    public org.bukkit.inventory.ItemStack asVoucher(String id) {
        return setTag(PUBLIC_BUKKIT_VALUES_VOUCHER_ID, new NBTTagString(id)).toBukkitCopy();
    }

    public String getVoucherId() {
        return ((NBTTagString) getTag(PUBLIC_BUKKIT_VALUES_VOUCHER_ID)).a_();
    }
}

