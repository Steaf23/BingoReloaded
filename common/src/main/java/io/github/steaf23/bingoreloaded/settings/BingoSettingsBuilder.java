package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.record.BingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class BingoSettingsBuilder {
	// empty when this builder has changed. The preset name is not stored in the actual settings, only used by the builder.
	private String preset;

	private final BingoSession session;
	private BingoCard card;
	private BingoGamemode mode;
	private CardSize cardSize;
	private int cardSeed;
	private PlayerKit kit;
	private EnumSet<EffectOptionFlags> effects;
	private int maxTeamSize;
	private int maxTeamCount;
	private BingoSettings.CountdownType countdownType;
	private int countdownGameDuration;
	private int hotswapGoal;
	private boolean expireHotswapTasks;
	private int completeGoal;
	private boolean differentCardPerTeam;
	private int blitzStartDuration;
	private int blitzBonusDuration;

	public BingoSettingsBuilder(BingoSession session) {
		this.session = session;

		BingoSettingsData data = new BingoSettingsData();
		BingoSettings def = data.getDefaultSettings();
		if (def == null) {
			ConsoleMessenger.error("Could not find default settings, make sure you have at least 1 existing settings preset and its set to be the default settings!");
			return;
		}
		fromOther(def, data.getDefaultSettingsName(), false);
	}

	public void fromOther(BingoSettings settings, String preset) {
		fromOther(settings, preset, true);
	}

	public void fromOther(BingoSettings settings, String preset, boolean sendUpdated) {
		card = settings.card();
		mode = settings.mode();
		cardSize = settings.size();
		cardSeed = settings.seed();
		kit = settings.kit();
		effects = settings.effects();
		maxTeamSize = settings.maxTeamSize();
		maxTeamCount = settings.maxTeamCount();
		countdownGameDuration = settings.countdownDuration();
		countdownType = settings.countdownType();
		hotswapGoal = settings.hotswapGoal();
		completeGoal = settings.completeGoal();
		differentCardPerTeam = settings.differentCardPerTeam();
		expireHotswapTasks = settings.expireHotswapTasks();
		blitzStartDuration = settings.blitzStartDuration();
		blitzBonusDuration = settings.blitzBonusDuration();
		if (sendUpdated) {
			settingsUpdated();
		}
		this.preset = preset;
	}

	public BingoSettingsBuilder applyVoteResult(VoteTicket voteResult) {
		BingoSettingsBuilder resultBuilder = new BingoSettingsBuilder(session);
		resultBuilder.fromOther(view(), ""); // it's not a preset anymore if we vote and change settings...

		BingoGamemode newMode = VoteTicket.CATEGORY_GAMEMODE.getValidResultOrNull(voteResult);
		if (newMode != null) {
			resultBuilder.mode = newMode;
		}

		PlayerKit newKit = VoteTicket.CATEGORY_KIT.getValidResultOrNull(voteResult);
		if (newKit != null) {
			resultBuilder.kit = newKit;
		}

		String newCard = VoteTicket.CATEGORY_CARD.getValidResultOrNull(voteResult);
		if (newCard != null) {
			resultBuilder.card = new BingoCard(newCard, resultBuilder.card.excludedTags());
		}

		CardSize newCardsize = VoteTicket.CATEGORY_CARDSIZE.getValidResultOrNull(voteResult);
		if (newCardsize != null) {
			resultBuilder.cardSize = newCardsize;
		}

		return resultBuilder;
	}

	public BingoSettingsBuilder cardName(String card) {
		if (!Objects.equals(this.card.cardName(), card)) {
			this.card = new BingoCard(card, this.card.excludedTags());
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder excludedTags(Set<String> tags) {
		if (!Objects.equals(this.card.excludedTags(), tags)) {
			this.card = new BingoCard(this.card.cardName(), tags);
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder mode(BingoGamemode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			// Blitz can only have a single team.
			if (mode == BingoGamemodes.BLITZ) {
				this.maxTeamCount = 1;
			}
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder cardSize(CardSize cardSize) {
		if (this.cardSize != cardSize) {
			this.cardSize = cardSize;
			// reset complete goal to remain full card completion settings by default
			this.completeGoal = cardSize.fullCardSize;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder cardSeed(int cardSeed) {
		if (this.cardSeed != cardSeed) {
			this.cardSeed = cardSeed;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder kit(PlayerKit kit) {
		if (this.kit != kit) {
			this.kit = kit;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder effects(EnumSet<EffectOptionFlags> effects) {
		this.effects = effects;
		settingsUpdated();
		return this;
	}

	public BingoSettingsBuilder toggleEffect(EffectOptionFlags effect, boolean enable) {
		if (enable)
			this.effects.add(effect);
		else
			this.effects.remove(effect);
		settingsUpdated();
		return this;
	}

	public BingoSettingsBuilder maxTeamSize(int maxTeamSize) {
		if (this.maxTeamSize != maxTeamSize) {
			this.maxTeamSize = maxTeamSize;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder maxTeamCount(int maxTeamCount) {
		// max team count cannot be changed in blitz.
		if (maxTeamCount == 1 || mode == BingoGamemodes.BLITZ) {
			return this;
		}

		if (this.maxTeamCount != maxTeamCount) {
			this.maxTeamCount = maxTeamCount;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder countdownType(BingoSettings.CountdownType countdownType) {
		if (this.countdownType != countdownType) {
			this.countdownType = countdownType;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder countdownGameDuration(int countdownGameDuration) {
		if (this.countdownGameDuration != countdownGameDuration) {
			this.countdownGameDuration = countdownGameDuration;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder blitzStartDuration(int blitzStartDuration) {
		if (this.blitzStartDuration != blitzStartDuration) {
			this.blitzStartDuration = blitzStartDuration;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder blitzBonusDuration(int blitzBonusDuration) {
		if (this.blitzBonusDuration != blitzBonusDuration) {
			this.blitzBonusDuration = blitzBonusDuration;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder expireHotswapTasks(boolean expireHotswapTasks) {
		if (this.expireHotswapTasks != expireHotswapTasks) {
			this.expireHotswapTasks = expireHotswapTasks;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder hotswapGoal(int hotswapGoal) {
		if (this.hotswapGoal != hotswapGoal) {
			this.hotswapGoal = hotswapGoal;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder completeGoal(int completeGoal) {
		if (this.completeGoal != completeGoal) {
			// clamp completeGoal based on card size as a goal of 25 on a small card is not possible...
			this.completeGoal = Math.min(completeGoal, cardSize.fullCardSize);
			settingsUpdated();
		}
		return this;
	}

	public BingoSettingsBuilder differentCardPerTeam(boolean value) {
		if (this.differentCardPerTeam != value) {
			this.differentCardPerTeam = value;
			settingsUpdated();
		}
		return this;
	}

	public BingoSettings view() {
		// Always generate a new id for the settings, by far the easiest way...
		return new BingoSettings(
				card,
				mode,
				cardSize,
				cardSeed,
				kit,
				effects,
				maxTeamSize,
				maxTeamCount,
				countdownType,
				countdownGameDuration,
				hotswapGoal,
				expireHotswapTasks,
				completeGoal,
				differentCardPerTeam,
				blitzStartDuration,
				blitzBonusDuration
		);
	}

	public void settingsUpdated() {
		preset = "";
		session.onSettingUpdated(view());
	}

	public String preset() {
		return preset;
	}
}
