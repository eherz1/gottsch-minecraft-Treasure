/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.treasure2.eventhandler;

import static com.someguyssoftware.treasure2.capability.TreasureCapabilities.CHARMABLE;
import static com.someguyssoftware.treasure2.capability.TreasureCapabilities.DURABILITY;
import static com.someguyssoftware.treasure2.capability.TreasureCapabilities.RUNESTONES;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.adornment.TreasureAdornmentRegistry;
import com.someguyssoftware.treasure2.capability.DurabilityCapability;
import com.someguyssoftware.treasure2.capability.ICharmableCapability;
import com.someguyssoftware.treasure2.capability.IDurabilityCapability;
import com.someguyssoftware.treasure2.capability.IRunestonesCapability;
import com.someguyssoftware.treasure2.capability.InventoryType;
import com.someguyssoftware.treasure2.capability.TreasureCapabilities;
import com.someguyssoftware.treasure2.item.Adornment;
import com.someguyssoftware.treasure2.item.KeyItem;
import com.someguyssoftware.treasure2.material.TreasureCharmableMaterials;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Mark Gottschling on Sep 6, 2020
 *
 */
public class AnvilEventHandler {
	// reference to the mod.
	private IMod mod;

	/**
	 * 
	 */
	public AnvilEventHandler(IMod mod) {
		setMod(mod);
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack leftStack = event.getLeft();
		ItemStack rightStack = event.getRight();

		// merge keys - add all uses/damage remaining in the right item to the left item.
		if (leftStack.getItem() == rightStack.getItem() && (leftStack.getItem() instanceof KeyItem)) {
			if (leftStack.hasCapability(DURABILITY, null)
					&& rightStack.hasCapability(DURABILITY, null)) {

				event.setCost(1);
				DurabilityCapability leftItemCap = (DurabilityCapability) leftStack.getCapability(DURABILITY, null);
				DurabilityCapability rightItemCap = (DurabilityCapability) rightStack.getCapability(DURABILITY, null);

				if (leftItemCap != null && rightItemCap != null) {
					int leftRemainingUses = leftItemCap.getDurability() - leftStack.getItemDamage();
					int rightRemainingUses = rightItemCap.getDurability() - rightStack.getItemDamage();
					ItemStack outputItem = new ItemStack(leftStack.getItem());

					DurabilityCapability outputItemCap = (DurabilityCapability) outputItem.getCapability(DURABILITY, null);

					int remainingUses = leftRemainingUses + rightRemainingUses;
					if (remainingUses > Math.max(leftItemCap.getDurability(), rightItemCap.getDurability())) {
						//						if (logger.isDebugEnabled()) {
						//							logger.debug("output has greater uses -> {} than emd -> {} - update emd", remainingUses, Math.max(leftItemCap.getEffectiveMaxDamage(), rightItemCap.getEffectiveMaxDamage()));
						//						}
						outputItemCap.setDurability(Math.max(leftItemCap.getDurability(), rightItemCap.getDurability()) + leftStack.getMaxDamage());
						outputItem.setItemDamage(leftStack.getItemDamage() + rightStack.getItemDamage());
					}
					else {
						if (remainingUses < Math.min(leftItemCap.getDurability(), rightItemCap.getDurability())) {
							outputItemCap.setDurability(Math.min(leftItemCap.getDurability(), rightItemCap.getDurability()));
						}
						else {
							outputItemCap.setDurability(Math.max(leftItemCap.getDurability(), rightItemCap.getDurability()));
						}
						outputItem.setItemDamage(outputItemCap.getDurability() - remainingUses);
					}
					event.setOutput(outputItem);
				}
			}
		}

		// add bindable (charm) to socketable (ex. adornment)
		else if (leftStack.hasCapability(CHARMABLE, null) && rightStack.hasCapability(CHARMABLE, null)
				&& leftStack.getCapability(CHARMABLE, null).isSocketable()
				&& rightStack.getCapability(CHARMABLE, null).isBindable()) {
			event.setCost(2);
			leftStack.getCapability(RUNESTONES, null).getEntities(InventoryType.INNATE).forEach(entity -> {
				Treasure.LOGGER.debug("binding charm: sourceStack.appliedTo -> {}", entity.getAppliedTo());
			});
			Optional<ItemStack> outStack = transferCapabilities(rightStack, leftStack, InventoryType.INNATE, InventoryType.SOCKET);
			if (outStack.isPresent()) {
				if (outStack.get().hasCapability(RUNESTONES, null)) {
					outStack.get().getCapability(RUNESTONES, null).getEntities(InventoryType.SOCKET).forEach(entity -> {
						Treasure.LOGGER.debug("binding charm: is applied -> {}", entity.isApplied());
						Treasure.LOGGER.debug("binding charm: applied to -> {}", entity.getAppliedTo());
						Treasure.LOGGER.debug("binding charm: applying runestone -> {} to entity -> {}", entity.getRunestone(), entity);
						entity.getRunestone().apply(outStack.get(), entity);
						Treasure.LOGGER.debug("binding charm: after apply: is applied -> {}", entity.isApplied());
						Treasure.LOGGER.debug("binding charm: after apply: applied to -> {}", entity.getAppliedTo());
						outStack.get().getCapability(CHARMABLE, null).getCharmEntities().forEach((type2, charm) -> {
							Treasure.LOGGER.debug("binding charm: entity -> {}, mana -> {}, max mana -> {}", charm.getCharm().getName().toString(), charm.getMana(), charm.getMaxMana());
						});
					});
				}
				event.setOutput(outStack.get());
			}
		}

		// add imbuing (charm) to imbuable (ex. adornment)
		else if (leftStack.hasCapability(CHARMABLE, null) && leftStack.getCapability(CHARMABLE, null).isImbuable()
				&& rightStack.hasCapability(CHARMABLE, null) && rightStack.getCapability(CHARMABLE, null).isImbuing()) {
			event.setCost(2);
			Optional<ItemStack> outStack = transferCapabilities(rightStack, leftStack, InventoryType.INNATE, InventoryType.IMBUE);
			if (outStack.isPresent()) {
				if (outStack.get().hasCapability(RUNESTONES, null)) {
					outStack.get().getCapability(RUNESTONES, null).getEntities(InventoryType.IMBUE).forEach(entity -> {
						entity.getRunestone().apply(outStack.get(), entity);
					});
				}
				event.setOutput(outStack.get());
			}
		}
		// add bindable (runestone) to socketable (ex. adornment)
		else if (leftStack.hasCapability(RUNESTONES, null) && rightStack.hasCapability(RUNESTONES, null)
				&& leftStack.getCapability(RUNESTONES, null).isSocketable()
				&& rightStack.getCapability(RUNESTONES, null).isBindable()) {
			event.setCost(2);
			rightStack.getCapability(RUNESTONES, null).getEntities(InventoryType.INNATE).forEach(entity -> {
				Treasure.LOGGER.debug("rightStack.appliedTo -> {}", entity.getAppliedTo());
			});
			Optional<ItemStack> stack = transferCapabilities(rightStack, leftStack, InventoryType.INNATE, InventoryType.SOCKET);
			if (stack.isPresent()) {
				AtomicBoolean isStackValid = new AtomicBoolean(true);
				stack.get().getCapability(RUNESTONES, null).getEntities(InventoryType.SOCKET).forEach(entity -> {
					Treasure.LOGGER.debug("is applied -> {}", entity.isApplied());
					Treasure.LOGGER.debug("applied to -> {}", entity.getAppliedTo());
					//					if(entity.getRunestone().isValid(stack.get()) && !entity.isApplied()) {
					Treasure.LOGGER.debug("applying runestone -> {} to entity -> {}", entity.getRunestone(), entity);
					Treasure.LOGGER.debug("applying cap...");
					
					if (!entity.getRunestone().isValid(stack.get())) {
						isStackValid.set(false);
						return;
					}
					entity.getRunestone().apply(stack.get(), entity);
					stack.get().getCapability(CHARMABLE, null).getCharmEntities().forEach((type, charm) -> {
						Treasure.LOGGER.debug("entity -> {}, mana -> {}, max mana -> {}, costEval -> {}", charm.getCharm().getName().toString(), charm.getMana(), charm.getMaxMana(), charm.getCostEvaluator().getClass().getSimpleName());
					});
					stack.get().getCapability(RUNESTONES, null).getEntities(InventoryType.SOCKET).forEach(stone -> {
						Treasure.LOGGER.debug("output runestone -> {}", stone);
					});
					//					}
					//					else {
					//						Treasure.logger.debug("runestone not applied.");
					//					}
				});
				if (isStackValid.get()) {
					event.setOutput(stack.get());
				}
			}
		}		
		// add gem to adornment
		else if ((leftStack.getItem() instanceof Adornment) 
				&&	leftStack.hasCapability(CHARMABLE, null)
				&&	TreasureCharmableMaterials.isSourceItemRegistered(rightStack.getItem().getRegistryName())) {
			//Treasure.logger.debug("left is adornment and right is gem!");
			ICharmableCapability cap = leftStack.getCapability(CHARMABLE, null);
			if (cap.getSourceItem().equals(Items.AIR.getRegistryName())) {
				event.setCost(1);
				event.setMaterialCost(1);

				// build the output item, duplicating the left stack (adornment) with the right stack as the source item
				Optional<Adornment> adornment = getAdornment(leftStack, rightStack);
				Treasure.LOGGER.debug("adornment -> {}", adornment.get().getRegistryName());
				if (adornment.isPresent()) {
					ItemStack outputStack = copyStack(leftStack, new ItemStack(adornment.get()));
					ICharmableCapability outputCap = outputStack.getCapability(CHARMABLE, null);
					outputCap.setHighestLevel(cap.getHighestLevel());
					event.setOutput(outputStack);
				}
			}
		}
		/*
		 * repair adornment
		 * for the right side, any adornment of any durability can be used as the material item.
		 * it will be destroyed along with all charms and runes in the repair process.
		 * the output will be generated with full durability.
		 * remember: durability also incorporates itemDamage. durability == maxDurability
		 */
		else if (leftStack.getItem() instanceof Adornment && rightStack.getItem() instanceof Adornment) {
			event.setCost(1);
			event.setMaterialCost(1);
			Treasure.LOGGER.debug("both are adornments");
			IDurabilityCapability leftcap = leftStack.getCapability(DURABILITY, null);
			Treasure.LOGGER.debug("leftStack current durability-> {}, max -> {}", leftcap.getDurability() - leftStack.getItemDamage(), leftcap.getMaxDurability());
			// create a new stack with full health/item damage.
			ItemStack outputStack = copyStack(leftStack, new ItemStack(leftStack.getItem()));
			IDurabilityCapability cap = outputStack.getCapability(DURABILITY, null);
			if (cap.getRepairs() > 0 && outputStack.getItemDamage() > 0) {
				// reset to full health (no item damage)
				outputStack.setItemDamage(0);
				// update repairs
				cap.setRepairs(cap.getRepairs() - 1);
				event.setOutput(outputStack);
				Treasure.LOGGER.debug("repairs -> {}, max repairs -> {}", cap.getRepairs(), cap.getMaxRepairs());
				Treasure.LOGGER.debug("current durability-> {}, max -> {}", cap.getDurability() - outputStack.getItemDamage(), cap.getMaxDurability());
			}
		}
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 * @param destInventoryType
	 * @return
	 */
	@Deprecated
	public static Optional<ItemStack> transferCapabilities(ItemStack source, ItemStack dest, InventoryType sourceType, InventoryType destType) {
		Treasure.LOGGER.debug("transfering caps...");

		// create a new dest item stack
		ItemStack stack = new ItemStack(dest.getItem());

		/*
		 * transfer existing state of dest to stack plus any relevant state from source to stack
		 */
		if (dest.hasCapability(DURABILITY, null)) {
			dest.getCapability(DURABILITY, null).copyTo(stack);
		}

		// transfer
		boolean charmSizeChanged = false;
		boolean runeSizeChanged = false;

		if (dest.hasCapability(CHARMABLE, null)) {
			stack.getCapability(CHARMABLE, null).clearCharms();			
			dest.getCapability(CHARMABLE, null).copyTo(stack);
			if (source.hasCapability(CHARMABLE, null)) {
				source.getCapability(CHARMABLE, null).transferTo(stack, sourceType, destType);
				// check if size has changed. indicates at least 1 charm was transfered. if not, return empty
				if (stack.getCapability(CHARMABLE, null).getCurrentSize(destType) > dest.getCapability(CHARMABLE, null).getCurrentSize(destType)) {
					charmSizeChanged = true;
				}
			}
		}

		if (dest.hasCapability(RUNESTONES, null)) {
			stack.getCapability(RUNESTONES, null).clear();
			Treasure.LOGGER.debug("before copyTo, runes size -> {}", stack.getCapability(RUNESTONES, null).getEntitiesCopy().size());
			dest.getCapability(RUNESTONES, null).copyTo(stack);
			Treasure.LOGGER.debug("after copyTo, runes size -> {}", stack.getCapability(RUNESTONES, null).getEntitiesCopy().size());
			if (source.hasCapability(RUNESTONES, null)) { // this is the rune
				Treasure.LOGGER.debug("source(runestone)'s runes ->");
				source.getCapability(RUNESTONES, null).getEntities(InventoryType.INNATE).forEach(entity -> {
					Treasure.LOGGER.debug("source entity -> {}", entity);
				});
				source.getCapability(RUNESTONES, null).transferTo(stack, sourceType, destType); // transfer from rune to output
				Treasure.LOGGER.debug("after transferTo, runes size -> {}", stack.getCapability(RUNESTONES, null).getEntitiesCopy().size());
				if (stack.getCapability(RUNESTONES, null).getCurrentSize(destType) > dest.getCapability(RUNESTONES, null).getCurrentSize(destType)) {
					runeSizeChanged = true;
				}
			}
		}
		if (charmSizeChanged | runeSizeChanged) {
			return Optional.of(stack);
		}
		return Optional.empty();
	}

	@Deprecated
	// use TreasureAdornments version
	private static Optional<Adornment> getAdornment(ItemStack baseStack, ItemStack stoneStack) {
		if (baseStack.hasCapability(TreasureCapabilities.CHARMABLE, null) && baseStack.getItem() instanceof Adornment) {
			ICharmableCapability cap = baseStack.getCapability(TreasureCapabilities.CHARMABLE, null);
			Adornment sourceAdornment = (Adornment) baseStack.getItem();
			return TreasureAdornmentRegistry.get(sourceAdornment.getType(), sourceAdornment.getSize(), cap.getBaseMaterial(), stoneStack.getItem().getRegistryName());
		}
		return Optional.empty();
	}

	/**
	 * 
	 * @param dest
	 * @param sourceItem
	 * @param charmEntities
	 * @return
	 */
	@Deprecated
	// use TreasureAdornments version
	private static ItemStack copyStack(final ItemStack source, final ItemStack dest) {
		ItemStack resultStack = dest.copy(); // <-- is this necessary?
		// save the source item
		ResourceLocation sourceItem = resultStack.getCapability(CHARMABLE, null).getSourceItem();

		// copy item damage
		resultStack.setItemDamage(source.getItemDamage());

		// copy the capabilities
		if (resultStack.hasCapability(DURABILITY, null)) {
			Treasure.LOGGER.debug("calling durability copyTo()");
			source.getCapability(DURABILITY, null).copyTo(resultStack);
		}

		if (dest.hasCapability(CHARMABLE, null)) {
			resultStack.getCapability(CHARMABLE, null).clearCharms();			
			source.getCapability(CHARMABLE, null).copyTo(resultStack);
		}

		if (dest.hasCapability(RUNESTONES, null)) {
			resultStack.getCapability(RUNESTONES, null).clear();			
			source.getCapability(RUNESTONES, null).copyTo(resultStack);
		}

		// reset the source item
		resultStack.getCapability(CHARMABLE, null).setSourceItem(sourceItem);

		return resultStack;
	}

	/*
	 * 
	 */
	public boolean doKeyMerge() {

		return true;
	}

	/**
	 * @return the mod
	 */
	public IMod getMod() {
		return mod;
	}

	/**
	 * @param mod the mod to set
	 */
	public void setMod(IMod mod) {
		this.mod = mod;
	}
}
