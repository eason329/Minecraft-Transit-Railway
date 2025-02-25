package mtr.data;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Random;
import java.util.function.Consumer;

public abstract class NameColorDataBase extends SerializedDataBase implements Comparable<NameColorDataBase> {

	public final long id;
	public String name;
	public int color;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_COLOR = "color";

	public NameColorDataBase() {
		this(0);
	}

	public NameColorDataBase(long id) {
		this.id = id == 0 ? new Random().nextLong() : id;
		name = "";
	}

	public NameColorDataBase(CompoundTag compoundTag) {
		id = compoundTag.getLong(KEY_ID);
		name = compoundTag.getString(KEY_NAME);
		color = compoundTag.getInt(KEY_COLOR);
	}

	public NameColorDataBase(FriendlyByteBuf packet) {
		id = packet.readLong();
		name = packet.readUtf(PACKET_STRING_READ_LENGTH).replace(" |", "|").replace("| ", "|");
		color = packet.readInt();
	}

	@Override
	public CompoundTag toCompoundTag() {
		final CompoundTag compoundTag = new CompoundTag();
		compoundTag.putLong(KEY_ID, id);
		compoundTag.putString(KEY_NAME, name);
		compoundTag.putInt(KEY_COLOR, color);
		return compoundTag;
	}

	@Override
	public void writePacket(FriendlyByteBuf packet) {
		packet.writeLong(id);
		packet.writeUtf(name);
		packet.writeInt(color);
	}

	public void update(String key, FriendlyByteBuf packet) {
		if (key.equals(KEY_NAME)) {
			name = packet.readUtf(PACKET_STRING_READ_LENGTH);
			color = packet.readInt();
		}
	}

	public void setNameColor(Consumer<FriendlyByteBuf> sendPacket) {
		final FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
		packet.writeLong(id);
		packet.writeUtf(KEY_NAME);
		packet.writeUtf(name);
		packet.writeInt(color);
		if (sendPacket != null) {
			sendPacket.accept(packet);
		}
	}

	@Override
	public int compareTo(NameColorDataBase compare) {
		return (name.toLowerCase() + color).compareTo((compare.name + compare.color).toLowerCase());
	}
}
