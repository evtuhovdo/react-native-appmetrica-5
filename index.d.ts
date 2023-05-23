declare type AppMetricaConfig = {
  apiKey: string
  appVersion?: string
  crashReporting?: boolean
  firstActivationAsUpdate?: boolean
  location?: Location
  locationTracking?: boolean
  logs?: boolean
  sessionTimeout?: number
  statisticsSending?: boolean
  preloadInfo?: PreloadInfo
  installedAppCollecting?: boolean
  maxReportsInDatabaseCount?: number
  nativeCrashReporting?: boolean
  activationAsSessionStart?: boolean
  sessionsAutoTracking?: boolean
}
declare type PreloadInfo = {
  trackingId: string
  additionalInfo?: object
}
declare type Location = {
  latitude: number
  longitude: number
  altitude?: number
  accuracy?: number
  course?: number
  speed?: number
  timestamp?: number
}
declare type AppMetricaDeviceIdReason =
  | "UNKNOWN"
  | "NETWORK"
  | "INVALID_RESPONSE"
declare const _default: {
  activate(config: AppMetricaConfig): void
  getLibraryApiLevel(): any
  getLibraryVersion(): any
  pauseSession(): void
  reportAppOpen(deeplink?: string | null): void
  reportError(error: string): void
  reportEvent(eventName: string, attributes?: object | null): void
  reportReferralUrl(referralUrl: string): void
  requestAppMetricaDeviceID(
    listener: (deviceId?: string, reason?: AppMetricaDeviceIdReason) => void
  ): void
  resumeSession(): void
  sendEventsBuffer(): void
  setLocation(location?: Location): void
  setLocationTracking(enabled: boolean): void
  setStatisticsSending(enabled: boolean): void
  setUserProfileID(userProfileID?: string): void
  changeCart(
    eventName: "addToCart" | "removeFromCart",
    price: number,
    id: string,
    productName: string,
    categories: string
  ): void
}
export default _default
