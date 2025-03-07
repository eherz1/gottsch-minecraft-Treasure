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
package com.someguyssoftware.treasure2.rune;

import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.capability.IDurabilityCapability;
import com.someguyssoftware.treasure2.capability.TreasureCapabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Jan 20, 2022
 *
 */
public class DurabilityRune extends Rune {

	protected DurabilityRune(Builder builder) {
		super(builder);
	}

	@Override
	public boolean isValid(ItemStack itemStack) {		
		return itemStack.hasCapability(TreasureCapabilities.DURABILITY, null);
	}

	@Override
	public void apply(ItemStack itemStack, IRuneEntity entity) {
		Treasure.LOGGER.debug("applying durability...");
		if (!isValid(itemStack) || entity.isApplied()) {
			return;
		}
		Treasure.LOGGER.debug("passed validity check...");
		if (itemStack.hasCapability(TreasureCapabilities.DURABILITY, null)) {
			IDurabilityCapability cap = itemStack.getCapability(TreasureCapabilities.DURABILITY, null);
			Treasure.LOGGER.debug("old durability -> {}", cap.getDurability());
			// NOTE: need to set the max durability first, else the new durability will be cutoff at the current max durability
			cap.setMaxDurability((int)Math.round(cap.getMaxDurability() * 1.25D));
			cap.setDurability((int)Math.round(cap.getDurability() * 1.25D));			
			Treasure.LOGGER.debug("new durability -> {}", cap.getDurability());
			entity.setApplied(true);
		}
	}

	@Override
	public void undo(ItemStack itemStack, IRuneEntity entity) {
		IDurabilityCapability cap = itemStack.getCapability(TreasureCapabilities.DURABILITY, null);
		cap.setDurability((int)Math.round(cap.getDurability() / 1.25D));
		cap.setMaxDurability((int)Math.round(cap.getMaxDurability() / 1.25D));
		entity.setApplied(false);
	}

	/*
	 * 
	 */
	public static class Builder extends Rune.Builder {
		public Builder(ResourceLocation name) {
			super(name);
		}
		@Override
		public IRune build() {
			return new DurabilityRune(this);
		}
	}
}
