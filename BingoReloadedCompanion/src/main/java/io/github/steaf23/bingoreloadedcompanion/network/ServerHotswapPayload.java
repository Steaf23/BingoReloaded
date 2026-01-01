package io.github.steaf23.bingoreloadedcompanion.network;

import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.HotswapTaskHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ServerHotswapPayload implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ServerHotswapPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "hotswap_tasks")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerHotswapPayload> CODEC = StreamCodec.ofMember(
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
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
