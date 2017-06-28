/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.util.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anyframe.util.scripts.DefaultScriptExecutor;
import org.anyframe.util.scripts.OsType;
import org.anyframe.util.system.SystemInfoUtil.MemRegion;
import org.anyframe.util.system.sigar.FileSystemInfo;
import org.anyframe.util.system.sigar.FileSystemUsageInfo;
import org.anyframe.util.system.sigar.ProcessInfo;
import org.anyframe.util.system.sigar.ProcessStatInfo;
import org.anyframe.util.system.sigar.SigarAccessor;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.shell.ShellCommandExecException;
import org.hyperic.sigar.shell.ShellCommandUsageException;
import org.junit.Before;
import org.junit.Test;

/**
 * TestCase for SystemInfoUtil
 * 
 * @author ByungHun Woo
 *
 */
public class SystemInfoUtilTest {

	@Before
	public void init() throws SigarException, IOException, ShellCommandUsageException, ShellCommandExecException {
		SigarAccessor.gatherSysInfo();
	}

	@Test
	public void testGetDiskPropertyList() {
		assertNotNull(SystemInfoUtil.getDiskProperty());
		List<FileSystemInfo> fileSystemInfo = SystemInfoUtil.getDiskProperty();
		for (FileSystemInfo fs : fileSystemInfo) {
			System.out.println(ReflectionToStringBuilder.reflectionToString(fs, ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	@Test
	public void testGetDiskProperty() {
		if (DefaultScriptExecutor.getOs().is(OsType.Windows)) {
			assertNotNull(SystemInfoUtil.getDiskProperty("c:/"));
			System.out.println(ReflectionToStringBuilder.reflectionToString(SystemInfoUtil.getDiskProperty("c:\\"),
					ToStringStyle.MULTI_LINE_STYLE));
		}
		else {
			assertNotNull(SystemInfoUtil.getDiskProperty("/"));
			System.out.println(ReflectionToStringBuilder.reflectionToString(SystemInfoUtil.getDiskProperty("/"),
					ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	@Test(expected = Exception.class)
	public void testGetDiskPropertyException() {
		assertNotNull(SystemInfoUtil.getDiskProperty("k:\\"));
	}

	@Test
	public void testGetDiskCapacityList() throws Exception {
		Map<String, FileSystemUsageInfo> diskCapacityMap = SystemInfoUtil.getDiskCapacity();
		Set<String> keySet = diskCapacityMap.keySet();
		Iterator<String> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			FileSystemUsageInfo fileSystemUsageInfo = diskCapacityMap.get(key);
			System.out.println("FileSystem Name" + key);
			System.out.println(ReflectionToStringBuilder.reflectionToString(fileSystemUsageInfo,
					ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	@Test
	public void testGetDiskCapacity() throws Exception {
		SystemInfoUtil.getDiskCapacity("testcase/srcDir");
		System.out.println(ReflectionToStringBuilder.reflectionToString(
				SystemInfoUtil.getDiskCapacity("testcase/srcDir"), ToStringStyle.MULTI_LINE_STYLE));
	}

	@Test
	public void testGetMountedDiskCapacity() throws Exception {
		if (DefaultScriptExecutor.getOs().is(OsType.Windows)) {
			SystemInfoUtil.getMountedDiskCapacity("C:");
			System.out.println(ReflectionToStringBuilder.reflectionToString(
					SystemInfoUtil.getMountedDiskCapacity("C:"), ToStringStyle.MULTI_LINE_STYLE));
		}
		else {
			SystemInfoUtil.getMountedDiskCapacity("/");
			System.out.println(ReflectionToStringBuilder.reflectionToString(SystemInfoUtil.getMountedDiskCapacity("/"),
					ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	@Test
	public void testGetSystemInfo() {
		assertNotNull(SystemInfoUtil.getSystemInfo());
		System.out.println(ReflectionToStringBuilder.reflectionToString(SystemInfoUtil.getSystemInfo(),
				ToStringStyle.MULTI_LINE_STYLE));
	}

	@Test
	public void testGetProcessStat() {
		ProcessStatInfo info = SystemInfoUtil.getProcessStat();
		assertTrue(info.getTotal() > 1);
		System.out.println(ReflectionToStringBuilder.reflectionToString(info, ToStringStyle.MULTI_LINE_STYLE));
	}

	@Test
	public void testGetProcessList() {
		Map<Long, ProcessInfo> processMap = SystemInfoUtil.getProcessList(null);
		assertFalse(processMap.isEmpty());

		Collection<ProcessInfo> col = processMap.values();
		Iterator<ProcessInfo> it = col.iterator();
		while (it.hasNext()) {
			ProcessInfo msg = it.next();
			System.out.println(ReflectionToStringBuilder.reflectionToString(msg.getProcessCpuInfo(),
					ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(ReflectionToStringBuilder.reflectionToString(msg.getProcessExeInfo(),
					ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(ReflectionToStringBuilder.reflectionToString(msg.getProcessStateInfo(),
					ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(ReflectionToStringBuilder.reflectionToString(msg.getProcessMemoryInfo(),
					ToStringStyle.MULTI_LINE_STYLE));
		}
	}

	@Test
	public void testGetProcessListArgs() {
		Map<Long, ProcessInfo> processMap = SystemInfoUtil.getProcessList(null);

		Collection<Long> col = processMap.keySet();
		Iterator<Long> it = col.iterator();
		String pid[] = new String[5];
		int index = 0;
		while (it.hasNext()) {
			pid[index] = String.valueOf(it.next());
			index++;
			if (index == 5)
				break;
		}

		Map<Long, ProcessInfo> processMapArgs = SystemInfoUtil.getProcessList(pid);
		assertFalse(processMapArgs.isEmpty());

		List<String> pss = SystemInfoUtil.getPs(pid);
		System.out.print(print(pss, 9));
	}

	@Test
	public void testGetPs() throws SigarException, ShellCommandUsageException, ShellCommandExecException {
		assertTrue(SystemInfoUtil.getPs(null).size() > 0);

		List<String> pss = SystemInfoUtil.getPs(null);
		System.out.print(print(pss, 9));
	}

	@Test
	public void testGetWho() throws SigarException, ShellCommandUsageException, ShellCommandExecException {
		assertTrue(SystemInfoUtil.getWho().size() > 0);

		List<String> who = SystemInfoUtil.getWho();
		for (String line : who) {
			System.out.println(line);
		}
	}

	@Test
	public void testGetMemoryCapacity() {
		assertNotNull(SystemInfoUtil.getMemoryCapacity(MemRegion.Total));
		assertNotNull(SystemInfoUtil.getMemoryCapacity(MemRegion.Used));
		assertNotNull(SystemInfoUtil.getMemoryCapacity(MemRegion.Free));
	}

	@Test
	public void testGetLs() {
		String ls = SystemInfoUtil.getLs("testcase/srcDir");
		// directory
		assertTrue(ls.startsWith("d\t"));
		System.out.print(ls);
	}

	@Test
	public void testGetLsList() {
		List<String> lsList = SystemInfoUtil.getLsList("testcase/srcDir");
		for (String ls : lsList)
			System.out.println(ls);
	}

	@Test
	public void testGetCpuInfo() {
		assertNotNull(SystemInfoUtil.getCpuInfo());
		System.out.println(ReflectionToStringBuilder.reflectionToString(SystemInfoUtil.getCpuInfo(),
				ToStringStyle.MULTI_LINE_STYLE));
	}

	@Test
	public void testGetClientIP() {

	}

	private static String print(List<String> info, int mod) {
		StringBuffer buf = new StringBuffer();
		Iterator<String> i = info.iterator();
		boolean hasNext = i.hasNext();
		int index = 0;
		while (hasNext) {
			index++;
			buf.append((String) i.next());
			hasNext = i.hasNext();
			if (index % mod == 0)
				buf.append("\n");
			else if (hasNext)
				buf.append("\t");

		}
		return buf.toString();
	}
}
