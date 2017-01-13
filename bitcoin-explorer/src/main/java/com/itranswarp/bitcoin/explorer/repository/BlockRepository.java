package com.itranswarp.bitcoin.explorer.repository;

import org.springframework.data.repository.CrudRepository;

import com.itranswarp.bitcoin.explorer.domain.BlockEntity;

public interface BlockRepository extends CrudRepository<BlockEntity, String> {

	/**
	 * Find last block.
	 * 
	 * @return Last block.
	 */
	BlockEntity findFirstByOrderByHeightDesc();

	/**
	 * Find block by height.
	 * 
	 * @param height
	 *            Block height.
	 * @return The block.
	 */
	BlockEntity findOneByHeight(long height);

}
