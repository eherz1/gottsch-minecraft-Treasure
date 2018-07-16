/**
 * 
 */
package com.someguyssoftware.treasure2.loot;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.enums.Rarity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Mark Gottschling on Jun 29, 2018
 *
 */
public class TreasureLootTables {

	public static LootContext CONTEXT;
	public static LootTable WITHER_CHEST_LOOT_TABLE;
	
	private static String CHEST_LOOT_TABLE_PATH = "chests";
	
	private static final List<String> CHESTS = ImmutableList.of(
			"armor_tool_chest",
			"food_potion_chest",
			"general_chest");
	
	/*
	 * Map of Loot Tables based on Rarity
	 */
	public static final Map<Rarity, List<LootTable>> lootTableMap = new HashMap<>();
	
	// list of loot table locations
	// TODO this is not dynamic and wouldn't be able to add more chests later
	// need to scan all the file names in the folder first
	private static final List<String> TABLES = ImmutableList.of(
			"chests/common/general_chest",			
			"chests/wither_chest"
			);
	
	static {
		Treasure.logger.debug("Registering loot tables");
		// register tables
		for (String s : TABLES) {
			Treasure.logger.debug("Registering loot table -> {}", s);
			LootTableList.register(new ResourceLocation(Treasure.MODID, s));
		}
	}
	// TODO add mapping: by chest name, by rarity etc - use a guava multimap
	
	/**
	 * 
	 */
	private TreasureLootTables(WorldServer world) {

	}
	
	/**
	 * 
	 * @param world
	 */
	public static void init(WorldServer world) {
		
		// TODO can be made at anytime - ie remains here, but the registering of loot tables occurs statically.
		// create a context
		CONTEXT = new LootContext.Builder(world).build();
		
		/*
		 *  TODO get the tables
		 *  need a map of tables based on Rarity
		 *  need a list of folders to load from
		 */
		WITHER_CHEST_LOOT_TABLE = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(Treasure.MODID + ":chests/wither_chest"));
		
//		List<ItemStack> stacks = TreasureLootTables.WITHER_CHEST_LOOT_TABLE.generateLootForPools(world.rand, TreasureLootTables.CONTEXT);
//		Treasure.logger.debug("Generated loot:");
//		for (ItemStack stack : stacks) {
//			Treasure.logger.debug(stack.getDisplayName());
//		}
		
//		Treasure.logger.debug("wither chest loot table: {}", WITHER_CHEST_LOOT_TABLE);
		
		// TODO map
		/////////////////
		try {
	        URI uri = Treasure.class.getResource("/assets/treasure2/loot_tables/chests/common").toURI();
	        Treasure.logger.debug("URI-> {}", uri);
	        
	        final Map<String, String> env = new HashMap<>();
	        final String[] array = uri.toString().split("!");
	        final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
	        final Path path = fs.getPath(array[1]);

	        // get all the files in the folder
	        Stream<Path> walk = Files.walk(path, 1);
	        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
	            Treasure.logger.debug(it.next());
	        }
	        // close the stream
	        walk.close();
	        
	        // close the file system
	        fs.close();

		}
		catch(Exception e) {
			Treasure.logger.error("error:", e);
		}
		
		
		//////////////

	}
}
