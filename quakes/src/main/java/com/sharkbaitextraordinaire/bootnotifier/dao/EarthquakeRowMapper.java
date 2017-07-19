package com.sharkbaitextraordinaire.bootnotifier.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.geojson.Point;
import org.springframework.jdbc.core.RowMapper;

import com.sharkbaitextraordinaire.bootnotifier.model.Earthquake;

public class EarthquakeRowMapper implements RowMapper<Earthquake> {

	@Override
	public Earthquake mapRow(ResultSet rs, int rowNum) throws SQLException {
		Earthquake quake = new Earthquake();
		quake.setMagnitude(rs.getDouble("magnitude"));
		quake.setPlace(rs.getString("magnitude"));
		quake.setEarthquaketime(rs.getLong("earthquaketime"));
		quake.setUpdate(rs.getLong("updatetime"));
		quake.setTz(rs.getInt("tz"));
		quake.setUrl(rs.getString("url"));
		quake.setDetail(rs.getString("detail"));
		quake.setFelt(rs.getInt("felt"));
		quake.setCdi(rs.getDouble("cdi"));
		quake.setTsunami(rs.getInt("tsunami"));
		quake.setSig(rs.getInt("sig"));
		quake.setCode(rs.getString("code"));
		quake.setIds(rs.getString("ids"));
		quake.setType(rs.getString("type"));
		quake.setTitle(rs.getString("title"));
		quake.setId(rs.getString("id"));
		quake.setLocation(new Point(rs.getDouble("longitude"), rs.getDouble("latitude")));
		return quake;
	}

}
