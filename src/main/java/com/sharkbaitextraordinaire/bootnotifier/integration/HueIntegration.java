package com.sharkbaitextraordinaire.bootnotifier.integration;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.sharkbaitextraordinaire.bootnotifier.config.HueConfig;

@Component
public class HueIntegration {

	@Autowired
	private HueConfig hueConfig;
	private PHHueSDK huesdk;
	private final Logger logger = LoggerFactory.getLogger(HueIntegration.class);

	public HueIntegration() {
	}

	@PostConstruct
	public void init() {
		huesdk = PHHueSDK.getInstance();
		huesdk.setAppName("SensorClient");
		//		huesdk.setAppName(hueConfig.getAppName());
		huesdk.setDeviceName("SensorClient");
		//		huesdk.setDeviceName(hueConfig.getDeviceName());
		huesdk.getNotificationManager().registerSDKListener(listener);

		connectToLastKnownAccessPoint();
	}

	private PHSDKListener listener = new PHSDKListener() {

		public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
			if (accessPointsList != null && accessPointsList.size() == 0) {
				logger.error("no bridges found");
			} else if (accessPointsList != null && accessPointsList.size() == 1) {
				if (!connectToLastKnownAccessPoint()) {
					logger.warn("One bridge found");
					PHAccessPoint accessPoint = accessPointsList.get(0);
					logger.warn("Access Point IP: " + accessPoint.getIpAddress());
					logger.warn("Access Point Bridge: " + accessPoint.getBridgeId());
				}
			}
		}

		public void onAuthenticationRequired(PHAccessPoint accessPoint) {
			logger.error("Go push the button on the bridge");
			PHHueSDK.getInstance().startPushlinkAuthentication(accessPoint);
		}

		public void onBridgeConnected(PHBridge bridge, String username) {
			logger.warn("Connected to bridge " + bridge + " as " + username);
			PHHueSDK.getInstance().setSelectedBridge(bridge);
			PHHueSDK.getInstance().enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
			String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
			logger.warn("username is: " + username);
			logger.warn("ip address is: " + lastIpAddress);
		}

		public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
			logger.debug(arg1.toString() + " updated cache");
		}

		public void onConnectionLost(PHAccessPoint arg0) {
			logger.warn("Lost connection to Hue at " + arg0.getIpAddress() + ", " + arg0.getBridgeId());
			// TODO do we need to reconnect?
		}

		public void onConnectionResumed(PHBridge arg0) {
			logger.debug("Resumed connection to Hue at " + arg0.getResourceCache().getBridgeConfiguration().getBridgeID());
		}

		public void onError(int code, String message) {
			if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
				logger.error("Bridge not responding");
				logger.error(message);
			} else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
				logger.error("Did you push the pushlink button?");
				logger.error(message);
			} else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
				logger.error("Authenticating to bridge failed");
				logger.error(message);
			} else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
				logger.error("Couldn't find the bridge");
				logger.error(message);
			}
			else logger.error(message);
		}

		public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
			for (PHHueParsingError parsingError : parsingErrorsList) {
				logger.error("ParsingError: " + parsingError.getMessage());
			}
		}
	};
	
	public boolean connectToLastKnownAccessPoint() {
		String lastIpAddress = hueConfig.getBridgeAddress();
		String username = hueConfig.getUsername();
		
		if (username == null || lastIpAddress == null) {
			logger.error("No information about connection to Hue available");
			return false;
		}
		
		PHAccessPoint accessPoint = new PHAccessPoint();
		accessPoint.setIpAddress(lastIpAddress);
		accessPoint.setUsername(username);
		logger.warn("Connecting to " + accessPoint.getIpAddress() + " with username " + accessPoint.getUsername());
		huesdk.connect(accessPoint);
		return true;
	}

	@PreDestroy
	public void shutDown() {
		try {
			huesdk.disableAllHeartbeat();
			huesdk.disconnect(huesdk.getSelectedBridge());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
