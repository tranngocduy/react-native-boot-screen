import * as Expo from "@expo/config-plugins";
type ExpoPlugin = Expo.ConfigPlugin<{
    assetsDir?: string;
    android?: {
        parentTheme?: "TransparentStatus" | "EdgeToEdge";
        darkContentBarsStyle?: boolean;
    };
}>;
export declare const withBootSplash: ExpoPlugin;
export {};
//# sourceMappingURL=expo.d.ts.map