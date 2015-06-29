package com.hyperfresh.mchyperchat.field;

import com.hyperfresh.mchyperchat.User;

public class DefaultFields
{
	public static class Name implements Field
	{
		@Override
		public String[] getFieldNames()
		{
			return new String[]{"name"};
		}

		@Override
		public String getFieldValue(User sender, String... args)
		{
			return sender.getName();
		}
	}

	public static class Message implements Field
	{
		@Override
		public String[] getFieldNames()
		{
			return new String[]{"message", "msg"};
		}

		@Override
		public String getFieldValue(User sender, String... args)
		{
			return sender.getLastSaid();
		}
	}
}