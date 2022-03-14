<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:template match="/">
		<h2 id="title">
			<xsl:value-of select="log/record/date"/>
		</h2>
		<!-- <label for="levelSelect">Logger level</label> -->
		<div id="before_table"></div>
		<div id="table-div">
			<table id="log-table">
				<tr>
					<th>Logger</th>
					<th>Level</th>
					<th>Time</th>
					<th>Message</th>
				</tr>

				<tr>
					<td id="loggerSelect-td">
						<input type="checkbox" id="children" value="children" checked="checked"/>
						Get children
						<select id='loggerSelect'>
							<option value='All' selected='selected'>All</option>
							<option value='Root.Observers'>Observers</option>
							<option value='Root.Trains'>Trains</option>
							<option value='Root.Trains.redTrain'>Trains.redTrain</option>
							<option value='Root.Trains.blueTrain'>Trains.blueTrain</option>
							<option value='Root.Trains.greenTrain'>Trains.greenTrain</option>
							<option value='Root.Trains.cargoTrain'>Trains.cargoTrain</option>
							<option value='Root.Railway'>Railway</option>
							<option value='Root.Railway.RailwayFactory'>Railway.RailwayFactory</option>
							<option value='Root.Railway.RouteFactory'>Railway.RouteFactory</option>
							<option value='Root.Railway.Selectrix'>Railway.Selectrix</option>
							<option value='Root.Railway.Signal'>Railway.Signal</option>
							<option value='Root.SX'>SX</option>
							<option value='Root.Simulation'>Simulation</option>
						</select>
					</td>
					<td id="levelSelect-td">
						<select id='levelSelect'>
							<option value='severe'>severe</option>
							<option value='warning'>warning</option>
							<option value='info' selected='selected'>info</option>
							<option value='config'>config</option>
							<option value='fine'>fine</option>
							<option value='finer'>finer</option>
							<option value='finest'>finest</option>
						</select>
					</td>
					<td></td>
					<td id="search">
						<input type="text" placeholder="Search.." name="search" id="search-input"
						       autocomplete="off"></input>
						<i class="fa fa-search" id="search-icon"></i>
					</td>
				</tr>

				<xsl:for-each select="log/record">
					<xsl:sort select="millis" order="descending"/>
					<tr class="log-tr {logger} {level}" logger="{logger}" level="{level}">
						<td class="logger">
							<xsl:value-of select="logger"/>
						</td>
						<td class="level">
							<xsl:value-of select="level"/>
						</td>
						<td class="millis">
							<xsl:value-of select="millis"/>
						</td>
						<td class="message">
							<xsl:value-of select="message"/>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</div>
	</xsl:template>
</xsl:stylesheet>
