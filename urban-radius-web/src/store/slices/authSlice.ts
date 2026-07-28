import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { clearToken, loadToken, login as loginRequest, saveToken } from '../../api/authApi';
import { fetchMe } from '../../api/userApi';
import { UserProfile } from '../../types/user';

export interface AuthState {
  token: string | null;
  user: UserProfile | null;
  loading: boolean;
  error: string | null;
  initialized: boolean;
}

const initialState: AuthState = {
  token: loadToken(),
  user: null,
  loading: false,
  error: null,
  initialized: false,
};

export const login = createAsyncThunk(
  'auth/login',
  async ({ email, password }: { email: string; password: string }, { rejectWithValue }) => {
    try {
      const { accessToken } = await loginRequest(email, password);
      saveToken(accessToken);
      const user = await fetchMe(accessToken);
      return { token: accessToken, user };
    } catch (error) {
      clearToken();
      const message = error instanceof Error ? error.message : 'Login failed';
      return rejectWithValue(message);
    }
  },
);

export const restoreSession = createAsyncThunk(
  'auth/restoreSession',
  async (_, { rejectWithValue }) => {
    const token = loadToken();
    if (!token) {
      return null;
    }

    try {
      const user = await fetchMe(token);
      return { token, user };
    } catch {
      clearToken();
      return rejectWithValue('Session expired');
    }
  },
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      clearToken();
      state.token = null;
      state.user = null;
      state.error = null;
    },
    clearAuthError(state) {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.token;
        state.user = action.payload.user;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.token = null;
        state.user = null;
        state.error = (action.payload as string) ?? 'Login failed';
      })
      .addCase(restoreSession.pending, (state) => {
        state.loading = true;
      })
      .addCase(restoreSession.fulfilled, (state, action) => {
        state.loading = false;
        state.initialized = true;
        if (action.payload) {
          state.token = action.payload.token;
          state.user = action.payload.user;
        }
      })
      .addCase(restoreSession.rejected, (state) => {
        state.loading = false;
        state.initialized = true;
        state.token = null;
        state.user = null;
      });
  },
});

export const { logout, clearAuthError } = authSlice.actions;
export default authSlice.reducer;
