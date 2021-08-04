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
package com.someguyssoftware.treasure2.capability;

import com.someguyssoftware.treasure2.Treasure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * @author Mark Gottschling on May 12, 2020
 *
 */
public class KeyRingCapabilityStorage implements Capability.IStorage<IKeyRingCapability> {
	private static final String IS_OPEN_TAG = "isOpen";
	
	@Override
	public INBT writeNBT(Capability<IKeyRingCapability> capability, IKeyRingCapability instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		try {
			nbt.putBoolean(IS_OPEN_TAG, instance.isOpen());
		} catch (Exception e) {
			Treasure.LOGGER.error("Unable to write state to NBT:", e);
		}
		return nbt;
	}

	@Override
	public void readNBT(Capability<IKeyRingCapability> capability, IKeyRingCapability instance, Direction side,
			INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT tag = (CompoundNBT) nbt;
			if (tag.contains(IS_OPEN_TAG)) {
				instance.setOpen(tag.getBoolean(IS_OPEN_TAG));
			}
		}		
	}
}
