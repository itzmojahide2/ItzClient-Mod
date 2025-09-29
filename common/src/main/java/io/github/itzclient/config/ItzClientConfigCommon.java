package io.github.itzclient.config;

import io.github.itzclient.ItzClientCommon;
import io.github.itzclient.AxolotlClientConfig.api.options.Option;
import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.StringOption;
import net.fabricmc.loader.api.FabricLoader;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class ItzClientConfigCommon {
	public enum MenuButtonMode {
		DISABLED,
		MODMENU () {
			@Override
			public boolean showButton() {
				return !(FabricLoader.getInstance().isModLoaded("modmenu") && !FabricLoader.getInstance().isModLoaded("itzclient-modmenu"));
			}
		},
		ALWAYS() { @Override public boolean showButton() { return true; } };
		@Override public String toString() { return "menu_button_mode."+super.toString().toLowerCase(Locale.ROOT); }
		public boolean showButton() { return false; }
	}

	public final OptionCategory config = OptionCategory.create("config");
	public final OptionCategory hidden = OptionCategory.create("storedOptions");
	public final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public final StringOption datetimeFormat = new StringOption("datetime_format", "yyyy/MM/dd HH:mm:ss", s -> dateTimeFormatter = DateTimeFormatter.ofPattern(s));
	public final EnumOption<MenuButtonMode> titleScreenOptionButtonMode = new EnumOption<>("title_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);
	public final EnumOption<MenuButtonMode> gameMenuScreenOptionButtonMode = new EnumOption<>("game_menu_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);

	private DateTimeFormatter dateTimeFormatter;

	public DateTimeFormatter getDateTimeFormatter() {
		if(dateTimeFormatter == null) {
			dateTimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat.get());
		}
		return dateTimeFormatter;
	}

	public static ItzClientConfigCommon instance() {
		return ItzClientCommon.getInstance().getConfig();
	}

	public final void add(Option<?> option) { config.add(option); }
	public final void addCategory(OptionCategory cat) { config.add(cat); }
	public final OptionCategory getConfig() { return config; }
}
