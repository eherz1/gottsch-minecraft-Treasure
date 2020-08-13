/**
 * 
 */
package com.someguyssoftware.treasure2.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.someguyssoftware.gottschcore.item.ModItem;
import com.someguyssoftware.treasure2.enums.Category;
import com.someguyssoftware.treasure2.enums.Rarity;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling onJan 10, 2018
 *
 */
public class LockItem extends ModItem {

	/*
	 * The category that the lock belongs to
	 */
	private Category category;

	/*
	 * The rarity of the lock
	 */
	private Rarity rarity;

	/*
	 * Flag if the lock is craftable
	 */
	private boolean craftable;

	/*
	 * a list of keys that unlock the lock
	 */
	private List<KeyItem> keys = new ArrayList<>(3);

	/**
	 * 
	 * @param item
	 * @param keys
	 */
	public LockItem(String modID, String name, Item.Properties properties, KeyItem[] keys) {
		this(modID, name, properties);
		getKeys().addAll(Arrays.asList(keys));
	}

	/**
	 * 
	 * @param item
	 */
	public LockItem(String modID, String name, Item.Properties properties) {
		super(modID, name, properties.group(ModItemGroups.MOD_ITEM_GROUP));
		setCategory(Category.BASIC);
		setRarity(Rarity.COMMON);
		setCraftable(false);
	}

	/**
	 * Format: Item Name (vanilla minecraft) Rarity: [COMMON | UNCOMMON | SCARCE |
	 * RARE| EPIC] [color = Dark Blue] Category: [...] [color = Gold] Craftable: [Yes |
	 * No] [color = Green | Dark Red] Accepts Keys: [list] [color = Gold]
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		tooltip.add(new TranslationTextComponent("tooltip.label.rarity",
				TextFormatting.DARK_BLUE + getRarity().toString()));
		tooltip.add(new TranslationTextComponent("tooltip.label.category", TextFormatting.GOLD + getCategory().toString()));

		ITextComponent craftable = null;
		if (isCraftable()) {
			craftable = new TranslationTextComponent("tooltip.yes").applyTextStyle(TextFormatting.GREEN);
		} else {
			craftable = new TranslationTextComponent("tooltip.no").applyTextStyle(TextFormatting.DARK_RED);
		}
		tooltip.add(new TranslationTextComponent("tooltip.label.craftable", craftable));

		// TODO this probably doesn't work
		String keyList = getKeys().stream().map(e -> e.getName().getFormattedText())
				.collect(Collectors.joining(","));

		tooltip.add(new TranslationTextComponent("tooltip.label.accepts_keys", TextFormatting.GOLD + keyList));
	}

	/**
	 * 
	 */
//	@Override
//	public ActionResultType onItemUse(ItemUseContext context) {
//
//		BlockPos chestPos = context.getPos();
//		// determine if block at pos is a treasure chest
//		Block block = context.getWorld().getBlockState(chestPos).getBlock();
//		if (block instanceof ITreasureChestProxy) {
//			chestPos = ((ITreasureChestProxy) block).getChestPos(chestPos);
//			block = context.getWorld().getBlockState(chestPos).getBlock();
//		}
//
//		if (block instanceof AbstractChestBlock) {
//			// get the tile entity
//			AbstractTreasureChestTileEntity te = (AbstractTreasureChestTileEntity) context.getWorld().getTileEntity(chestPos);
//
//			// exit if on the client
//			if (WorldInfo.isClientSide(context.getWorld())) {
//				return EnumActionResult.FAIL;
//			}
//
//			try {
//				ItemStack heldItem = context.getPlayer().getHeldItem(context.getHand());
//				// handle the lock
//				// NOTE don't use the return boolean as the locked flag here, as the chest is
//				// already locked and if the method was
//				// unsuccessful it could state the chest is unlocked.
//				handleHeldLock(te, context.getPlayer(), heldItem);
//			} catch (Exception e) {
//				Treasure.LOGGER.error("error: ", e);
//			}
//		}
//		return super.onItemUse(context);
//	}

	/**
	 * 
	 * @param te
	 * @param player
	 * @param heldItem
	 * @return flag indicating if a lock was added
	 */
//	private boolean handleHeldLock(AbstractTreasureChestTileEntity te, PlayerEntity player, ItemStack heldItem) {
//		boolean lockedAdded = false;
//		LockItem lock = (LockItem) heldItem.getItem();
//		// add the lock to the first lockstate that has an available slot
//		for (LockState lockState : te.getLockStates()) {
//			if (lockState != null && lockState.getLock() == null) {
//				lockState.setLock(lock);
//				te.sendUpdates();
//				// decrement item in hand
//				heldItem.shrink(1);
////				if (heldItem.getCount() <=0 && !player.capabilities.isCreativeMode) {
////					IInventory inventory = player.inventory;
////					inventory.setInventorySlotContents(player.inventory.currentItem, null);
////				}
//				lockedAdded = true;
//				break;
//			}
//		}
//		return lockedAdded;
//	}

	/**
	 * 
	 * @param keyItem
	 * @return
	 */
	public boolean acceptsKey(KeyItem keyItem) {
		for (KeyItem k : getKeys()) {
			if (k == keyItem)
				return true;
		}
		return false;
	}

	/**
	 * @return the rarity
	 */
	public Rarity getRarity() {
		return rarity;
	}

	/**
	 * @param rarity the rarity to set
	 */
	public LockItem setRarity(Rarity rarity) {
		this.rarity = rarity;
		return this;
	}

	/**
	 * @return the craftable
	 */
	public boolean isCraftable() {
		return craftable;
	}

	/**
	 * @param craftable the craftable to set
	 */
	public LockItem setCraftable(boolean craftable) {
		this.craftable = craftable;
		return this;
	}

	/**
	 * @return the keys
	 */
	public List<KeyItem> getKeys() {
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public LockItem setKeys(List<KeyItem> keys) {
		this.keys = keys;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LockItem [name=" + getRegistryName() + ", rarity=" + rarity + ", craftable=" + craftable + ", keys="
				+ keys + "]";
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public LockItem setCategory(Category category) {
		this.category = category;
		return this;
	}
}
