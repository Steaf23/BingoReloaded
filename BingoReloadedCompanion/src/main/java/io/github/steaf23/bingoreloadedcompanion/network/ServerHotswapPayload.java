package io.github.steaf23.bingoreloadedcompanion.network;

import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.HotswapTaskHolder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ServerHotswapPayload implements CustomPayload {

	public static final CustomPayload.Id<ServerHotswapPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "hotswap_tasks")
	);

	public static final PacketCodec<RegistryByteBuf, ServerHotswapPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {}, // Packet will not be sent, only received.
			buf -> {
				List<HotswapTaskHolder> holders = new ArrayList<>();

				int arrSize = buf.readInt();
				for (int i = 0; i < arrSize; i++) {
					long total = buf.readLong();
					long current = buf.readLong();
					boolean recovering = buf.readBoolean();
					boolean expires = buf.readBoolean();
					holders.add(new HotswapTaskHolder(total, current, recovering, expires));
				}

				return new ServerHotswapPayload(ImmutableList.copyOf(holders));
			}
	);

	public final ImmutableList<HotswapTaskHolder> holders;

	public ServerHotswapPayload(ImmutableList<HotswapTaskHolder> holders) {
		this.holders = holders;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
