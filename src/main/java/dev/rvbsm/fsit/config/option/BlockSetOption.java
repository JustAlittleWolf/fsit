package dev.rvbsm.fsit.config.option;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockSetOption extends SimpleOption<Set<String>> {

	public BlockSetOption(String key, Set<String> value) {
		super(key, value);
	}

	public Set<Block> getBlocks() {
		final Set<String> blockNames = super.getValue();

		return blockNames
						.stream()
						.map(Identifier::new)
						.map(Registries.BLOCK::get)
						.filter(block -> !(block instanceof AirBlock))
						.collect(Collectors.toSet());
	}
}
